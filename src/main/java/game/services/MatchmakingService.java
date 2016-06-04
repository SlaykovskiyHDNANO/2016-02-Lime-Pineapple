package game.services;

import game.GameRoom;
import game.PlayingUser;
import game.RoomStatus;
import game.exceptions.GameException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import server.messaging.Message;
import server.messaging.MessageService;
import server.messaging.MessageType;
import server.messaging.messages.SystemMessage;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * created: 5/25/2016
 * package: game.services
 */
public class MatchmakingService {
    protected static class MESSAGES {
        public static final String CREATE_ROOM_REQUEST = "Matchmaking.Room.Create";
        public static final String LEAVE_ROOM_REQUEST = "Matchmaking.Room.Leave";
        public static final String JOIN_ROOM_REQUEST = "Matchmaking.Room.Join";
        public static final String LIST_ROOMS_REQUEST ="Matchmaking.List.Rooms";
        public static final String PLAYER_READY = "";
        public static final String JOIN_ROOM_RESPONSE = "Matchmaking.JoinRoom.Response";
    }

    static final Logger LOGGER = LogManager.getLogger();

    final Map<Long, GameRoom> activeRooms = new ConcurrentHashMap<>();
    final AtomicLong counter = new AtomicLong(0L);
    final AtomicLong userCounter=new AtomicLong(0L);
    final Map<Long, PlayingUser> activeUsers = new ConcurrentHashMap<>();
    final Map<PlayingUser, GameRoom> userToRooms = new ConcurrentHashMap<>();

    final MessageService service;
    PlayingUser waitingUser=null;
    final GameCardService gameCardService;
    final Lock lock = new ReentrantLock();


    public MatchmakingService(@NotNull GameCardService gameCardService, @NotNull MessageService service) {
        this.gameCardService = gameCardService;
        this.service = service;
    }

    public void configure() {
        this.service.subscribe(SystemMessage.MESSAGES.CLIENT_DISCONNECTED, (sender, message) -> {
            if(Objects.equals(waitingUser, sender.getUser())) {
                this.waitingUser = null;
            }
        });

        this.service.subscribe(MESSAGES.JOIN_ROOM_REQUEST, (sender, message) -> {
            // TODO: do something here
            lock.lock();
            if (waitingUser==null) waitingUser=sender.getUser();
            else {
                final long newRoomId=counter.incrementAndGet();
                // Создаём комнату с 12 картами на руках у каждого чувака. Сами карты ещё не создаются
                final GameRoom gameRoom=new GameRoom(newRoomId, (short) 12);
                // Соединяем их с комнатой
                waitingUser.setCurrentRoomId(newRoomId);
                sender.getUser().setCurrentRoomId(newRoomId);
                try {
                    gameRoom.addUser(waitingUser);
                    gameRoom.addUser(sender.getUser());
                } catch (GameException e) {
                    LOGGER.warn(String.format("Shiiiiitt. Could not add users to their rooms:%n%s", e.toString()));
                    return;
                }
                gameRoom.setRoomStatus(RoomStatus.INITIAL_PHASE);
                // Добавляем комнату в очередь грузящихся
                this.userToRooms.put(waitingUser, gameRoom);
                this.userToRooms.put(sender.getUser(), gameRoom);
                // Наконец, отправляем ответ на каждому плееру - теперь они грузятся

                try {
                    service.sendMessage(gameRoom, new Message(MessageType.MATCHMAKING, MESSAGES.JOIN_ROOM_RESPONSE));
                }
                catch (IOException e) {
                    LOGGER.warn(String.format("God, could not send message to room:%n%s", e.toString()));
                }
                // Очищаем ожидающего противника юзера
                waitingUser=null;
            }
            lock.unlock();
        });

        this.service.subscribe(MESSAGES.PLAYER_READY, (sender, message)->{
            final PlayingUser playingUser = sender.getUser();
            final GameRoom room = this.userToRooms.getOrDefault(playingUser, null);
            if (room == null) {
                LOGGER.warn(String.format("[ W ] WTF, room is null when player is ready. Have you added the room?%nSender PlayingUser is null?=%b", playingUser==null));
                return;
            }
            this.userToRooms.remove(playingUser);
            if (this.userToRooms.getOrDefault(playingUser, null) == null) {
                this.service.addActiveRoom(room);
                room.setRoomStatus(RoomStatus.GAME_PHASE);
            }
        });
    }


    /*void createRoom(PlayingUser founder, short cards) {
        final long newRoomId=counter.incrementAndGet();
        founder.setRoom(newRoomId);
        activeRooms.put(newRoomId, new GameRoom(founder,cards));
        activeRooms.get(newRoomId).setId(newRoomId);
    }




    Collection<GameRoom> showRooms() {
        return activeRooms.values();
    }

    Collection<GameRoom> availableRooms() {

        final Collection<GameRoom> waiting=new ArrayList<>();
        waiting.add(new GameRoom(new PlayingUser(new User("11","11")),(short) 1));
        for (Long id : activeRooms.keySet()) {
            if (activeRooms.get(id).getRoomStatus()== RoomStatus.LOOKING_FOR_PEOPLE) {
                waiting.add(activeRooms.get(id));
            }
        }
        return waiting;
    }



    public long getRoomIdByUser(PlayingUser user) {
        return user.getCurrentRoomId();
    }

    @Nullable
    public PlayingUser getUserOpponent(PlayingUser user) {
        if (user.getCurrentRoomId()==-1L) return null;
        final GameRoom userRoom=activeRooms.get(user.getCurrentRoomId());
        return userRoom.getOpponent(user);
    }
    public void userExitedRoom(PlayingUser user) {
        if (user.getCurrentRoomId()!=-1L) {
            activeRooms.get(user.getCurrentRoomId()).userExited(user);
            user.setRoom(-1L);
        }
    }*/
}
