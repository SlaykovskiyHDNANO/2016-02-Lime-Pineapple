package game.services;

import com.google.gson.Gson;
import db.exceptions.DatabaseException;
import db.models.User;
import db.services.AccountService;
import game.Card;
import game.GameField;
import game.GameRoom;
import game.PlayingUser;
import game.exceptions.GameException;
import game.services.messages.EndGameMessageResponse;
import game.services.messages.GameMessageDeserializer;
import game.services.messages.PlayerActMessage;
import game.services.messages.PlayerActResponseMessage;
import javassist.NotFoundException;
import server.messaging.Message;
import server.messaging.MessageType;
import server.messaging.messages.SystemMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import server.messaging.Client;
import server.messaging.MessageService;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * created: 5/25/2016
 * package: game.services
 */
public class GameEngineService {
    public static final String SKIP_MESSAGE="Game.PlayerAct.Response.Skipped";
    public static final String FIRST_PLAYER = "Game.Player.First";
    private static final String PLAYER_ACT_ENEMY_ACTED = "Game.Player.Enemy.Acted";

    static final Logger LOGGER = LogManager.getLogger();
    final MessageService messageService;
    final AccountService accountService;
    final GameMessageDeserializer deserializer;
    final GameCardService gameCardService;
    private final GameFieldFactoryService gameFieldFactoryService;

    public GameEngineService(@NotNull GameMessageDeserializer deserializer,
                      @NotNull MessageService messageService,
                      @NotNull AccountService accountService,
                             @NotNull GameCardService gameCardService,
                             @NotNull GameFieldFactoryService gameFieldFactoryService) {
        this.messageService = messageService;
        this.accountService = accountService;
        this.deserializer = deserializer;
        this.gameCardService = gameCardService;
        this.gameFieldFactoryService = gameFieldFactoryService;
    }

    // Конфигурирует сервис: запускает слушатели событий на сервисе сообщений
    public void configure() {
        this.messageService.subscribe(SystemMessage.MESSAGES.CLIENT_DISCONNECTED, (client, message)->{
            this.onClientDisconnected(client);
        });
        this.messageService.subscribe(PlayerActMessage.MESSAGE_NAME, (client, message) -> {
            final PlayerActMessage gm = (PlayerActMessage) deserializer.deserialize(message);
            this.onPlayerAct(client, gm);
        });
        this.messageService.onNewActiveRoom((room) -> {
            // initialize room
            final PlayingUser[] users = (PlayingUser[]) room.getUsers().toArray();
            final Map<PlayingUser, List<Card>> hands = new ConcurrentHashMap<>();
            final GameField gameField = gameFieldFactoryService.makeField(users);
            for (PlayingUser user : users ){
                hands.put(user,this.gameCardService.makeHand(room.getCardsInHandByPlayer(), user));
            }
            room.initializeWithGameValues(hands, gameField );
            final int firstPlayer = (int) (Math.round(Math.random()*users.length))-1;
            try {
                this.messageService.sendMessage(users[firstPlayer], new Message(MessageType.GAME, FIRST_PLAYER));
            } catch (Throwable t) {
                LOGGER.warn(String.format("[ W ] Exception during sending message of first player to client:%n%s", t.toString()));
            }
        });
    }


    // METHODS



    // ENGINE LOGIC GAME


    void endGame(@NotNull GameRoom room, @NotNull PlayingUser winnerClient, @NotNull EndGameReason reason) {
        final PlayingUser winner = winnerClient;
        final PlayingUser loser = room.getAnotherPlayer(winnerClient);
        final User winnerUser = winner.getLinkedUser();
        final User loserUser = loser.getLinkedUser();
        // обновим данные победителя
        try {
            this.accountService.updateUserStats(winnerUser, winner.getCurrentScore());
        } catch (DatabaseException e) {
            LOGGER.warn(String.format("[ W ] Exception during user stats update: %s", e.toString()));
        }
        // отправляем сообщения об окончании игры
        try {
            switch (reason) {
                case DISCONNECT:
                    try {
                        this.messageService.sendMessage(winnerClient, new EndGameMessageResponse(winnerUser, reason));
                    }
                    catch (NotFoundException e){
                        LOGGER.warn(e.getMessage());
                    }
                    break;
                case WE_HAVE_A_WINNER:
                    // обновим данные проигравшего, но только если он не отключался
                    try {
                        this.accountService.updateUserStats(loserUser, loser.getCurrentScore());
                    } catch (DatabaseException e) {
                        LOGGER.warn(String.format("[ W ] Exception during user stats update: %s", e.toString()));
                    }
                    this.messageService.sendMessage(room, new EndGameMessageResponse(winnerUser, reason));
                    break;
                default:
                    LOGGER.warn("[ W ] Wow, end game reason is default?");
                    this.messageService.sendMessage(room, new EndGameMessageResponse(null, reason));
                    break;
            }
        } catch (IOException e) {
            LOGGER.warn(String.format("[ W ]Could not send message to endpoint. WTF?%n%s", e.toString()) );
        }

    }

    void endAsStalemate(@NotNull GameRoom room, @NotNull EndGameReason reason) {
        for (PlayingUser user : room.getUsers()) {
            try {
                this.accountService.updateUserStats(user.getLinkedUser(), user.getCurrentScore());
            } catch (DatabaseException e) {
                LOGGER.warn(String.format("[ W ] Error saving model: %n%s", e.toString()));
            }
        }
        this.messageService.trySendMessage(room, new EndGameMessageResponse(null, reason));
    }



    int winIndex(PlayingUser[] users) {
        int bestScore = users[0].getCurrentScore();
        int winner = 0;
        for (int i = 1, s = users.length; i<s; ++i) {
            final int us = users[i].getCurrentScore();
            if( us > bestScore) {
                winner = i;
                bestScore = us;
            } else if(us == bestScore) {
                winner = -1;
            }
        }
        return winner;
    }



    void endRound(GameRoom room) {
        final PlayingUser[] users = (PlayingUser[]) room.getUsers().toArray();
        final int index = winIndex(users);
        final EndGameReason reason = index == -1 ? EndGameReason.STALEMATE : EndGameReason.WE_HAVE_A_WINNER;
        if (reason == EndGameReason.STALEMATE) {
            this.endAsStalemate(room, reason);
        } else {
            this.endGame(room, users[index], reason);
        }
    }

    void playerAct(@NotNull GameRoom room, @NotNull PlayingUser actor, @NotNull PlayerActMessage.PlayerActData actData) {
        // выполняем действия до действия юзера
        final Gson messInfo= new Gson();
        if (actData.skippedTurn) {
            try {
                actor.setSkipped(true);
                if (room.getAnotherPlayer(actor).isSkipped()) {
                     endRound(room);
                }
                try {
                    this.messageService.sendMessage(room.getAnotherPlayer(actor), new Message(MessageType.GAME, SKIP_MESSAGE));
                }
                catch (NotFoundException e) {
                    LOGGER.warn(e.getMessage());
                }

            }
            catch (IOException e) {
                System.out.println(e.getMessage());// обрабатываем действия юзера
            }
        }
        else if (actData.activatedBossCard) {
            actor.setSkipped(false);
            LOGGER.info("[ I ] Boss card activated! ");
            //room.activateBossCard(actor);
            //try {
            //    if (room.activateBossCard(actor)) {
            //        this.messageService.sendMessage(room,  new Message(MessageType.GAME,messInfo.toJson(room.getHand(actor))));
            //    }
            //    this.messageService.sendMessage(room, new Message(MessageType.GAME,messInfo.toJson(room.getCurrentField())));
            //} catch (IOException e) {
            //    System.out.println(e.getMessage());// обрабатываем действия юзера
            //}
        }
        else {
            this.messageService.trySendMessage(actor, new PlayerActResponseMessage(true));
            // TODO: make honest mechanics
            try {
                final GameField field = room.getCurrentField();
                final Card activeCard = room.popCardFromHand(actor, Math.toIntExact(actData.playerCardId));
                actor.setCurrentScore(actor.getCurrentScore()+activeCard.getModel().getScore());
                field.putCard(
                        activeCard,
                        field.translateRowIndexToRowOwner(actor, actData.rowIndex) ,
                        actData.columnIndex);
                this.messageService.trySendMessage(room, new PlayerActMessage(PLAYER_ACT_ENEMY_ACTED, actData));
            } catch (GameException e) {
                LOGGER.warn(String.format("Exception during altering room state:%n%s", e.toString()));
            }

            /// /room.placeCard(actor, actData.playerCardId, actData.rowIndex, actData.columnIndex);
            //try {
//
            //    this.messageService.sendMessage(room, new Message(MessageType.GAME, messInfo.toJson(room.getCurrentField())));
            //} catch (IOException e) {
            //    System.out.println(e.getMessage());// обрабатываем действия юзера
            //}
        }
    }


    // MESSAGE HANDLERS

    private void onPlayerAct(Client client, PlayerActMessage message) {
        try {
            final GameRoom room = messageService.getRoom(client);
            assert (PlayerActMessage.PlayerActData) message.getData() != null;
            this.playerAct(room, client.getUser(), (PlayerActMessage.PlayerActData) message.getData());
        } catch (NotFoundException t) {
            LOGGER.warn(String.format("[ W ] Exception during event handler: onPlayerAct. Malformed message?%n%s", t.toString()));
        }

    }


    private void onClientDisconnected(Client client) {
        try {
            final GameRoom room = messageService.getRoom(client);
            this.endGame(room, client.getUser(), EndGameReason.DISCONNECT);
        } catch (NotFoundException t) {
            LOGGER.warn(String.format("[ W ] Exception during event handler: onClientDisconnected(). Is client without room disconnected?%n%s", t.toString()));
        }

    }

    // INNER CLASSES

    public enum EndGameReason {
        DISCONNECT,
        WE_HAVE_A_WINNER,
        STALEMATE
    }

}
