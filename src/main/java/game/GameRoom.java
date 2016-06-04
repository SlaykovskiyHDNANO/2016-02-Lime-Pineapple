package game;

import db.models.User;
import game.exceptions.GameException;
import game.services.GameCardService;
import game.services.GameFieldFactoryService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

// ВНИМАНИЕ: это простая модель, она не несёт никакой логики
@SuppressWarnings("CallToSimpleSetterFromWithinClass")
public class GameRoom {
    static final Logger LOGGER = LogManager.getLogger(GameRoom.class);

    // ROOM STATS
    final long id;
    RoomStatus roomStatus = RoomStatus.LOOKING_FOR_PEOPLE;
    final PlayingUser[] users = new PlayingUser[2];

    // INNER STATE
    final ReadWriteLock rwLock = new ReentrantReadWriteLock();

    //GAME VALUES
    final short cardsInHandByPlayer; // количество кард у одного игрока в руке

    GameField field   = null;
    Map<PlayingUser, List<Card>> playerHands = null; // карты в руке игрока


    @NotNull
    public RoomStatus getRoomStatus() {
        return this.roomStatus;
    }
    // просто меняет состояние комнаты. Ничего не отправляем
    public void setRoomStatus(@NotNull RoomStatus status) {
        this.roomStatus = status;
    }

    public long getId() {
        return id;
    }

    public GameRoom(long id, short totalHandCards) {
        this.id = id;
        this.cardsInHandByPlayer = totalHandCards;
    }

    public short getCardsInHandByPlayer() {
        return cardsInHandByPlayer;
    }

    public void addUser(@NotNull PlayingUser user) throws GameException {
        rwLock.writeLock().lock();
        for (int i = 0, s = this.users.length; i<s; ++i) {
            if (this.users[i] == null) {
                this.users[i] = user;
            }
        }
        throw new GameException("Room is full");
    }

    // USE THIS AFTER TWO PLAYERS HAVE STARTED THE GAME
    public synchronized void initializeWithGameValues(@NotNull Map<PlayingUser, List<Card>> hands,
                                                      @NotNull GameField gameField) {
        // check two fields
        this.playerHands = hands;
        this.field = gameField;
    }




    @NotNull
    public List<Card> getHand(PlayingUser user) {
        return this.playerHands.get(user);
    }

    @NotNull
    public Set<Map.Entry<PlayingUser, List<Card>>> getPlayerHands() {
        return this.playerHands.entrySet();
    }

    @NotNull
    public PlayingUser getAnotherPlayer(PlayingUser thisPlayer) {
        for (PlayingUser user : this.users) {
            if (!Objects.equals(user, thisPlayer)) {
                return user;
            }
        }
        throw new RuntimeException("Could not find another user. Every user in room equals this user!");
    }

    public boolean hasUser(PlayingUser user) {
        return Arrays.asList(users).contains(user);
    }


    public boolean isEmpty() {
        for (PlayingUser user : this.users) {
            if (user != null) {
                return false;
            }
        }
        return true;
    }

    public void removeUser(PlayingUser user) {
        rwLock.writeLock().lock();
        for (int i = 0, s = this.users.length; i<s; ++i) {
            if (Objects.equals(this.users[i], user))
                this.users[i] = null;
        }
        rwLock.writeLock().unlock();
    }

    public Set<PlayingUser> getUsers() {
        rwLock.readLock().lock();
        final Set<PlayingUser> set = Collections.synchronizedSet(new HashSet<>());
        try {
            Collections.addAll(set, this.users);
            return set;
        } catch (Throwable e) {
            LOGGER.warn(String.format("Exception during adding user into room.%n%s", e.toString()));
            rwLock.writeLock().unlock();
            throw e;
        }
    }

    public GameField getCurrentField() {
        return field;
    }

    public synchronized Card popCardFromHand(@NotNull PlayingUser actor, @NotNull Integer playerCardId) throws GameException {
        final List<Card> hand = this.playerHands.get(actor);
        for (int i = 0, s = hand.size(); i<s; ++i) {
            final Card card = hand.get(i);
            if (Objects.equals(card.id, playerCardId)) {
                hand.remove(i);
                return card;
            }
        }
        throw new GameException("Card with this id not found. Is this is valid user for a card?");
    }
}
