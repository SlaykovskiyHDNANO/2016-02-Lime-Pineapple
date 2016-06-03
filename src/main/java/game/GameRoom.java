package game;

import db.models.User;
import game.services.GameCardService;
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

    final GameCardService gameCardService;
    final ReadWriteLock rwLock = new ReentrantReadWriteLock();
    GameField field=new GameField(new GameFieldRow[4]);
    boolean eviluser;

    //GAME VALUES
    private final short cardsInHandByPlayer; // количество кард у одного игрока в руке

    private final Map<PlayingUser, Card[]> playerHands = new ConcurrentHashMap<>(); // карты в руке игрока
    private final Map<PlayingUser, BossCard> bossCards=new ConcurrentHashMap<>();


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

    public GameRoom(@NotNull GameCardService gameCardService, long id, short totalHandCards) {
        this.id = id;
        this.cardsInHandByPlayer = totalHandCards;
        this.gameCardService = gameCardService;
        eviluser=false;
    }

    public short getCardsInHandByPlayer() {
        return cardsInHandByPlayer;
    }

    public void addUser(@NotNull PlayingUser user) {
        rwLock.writeLock().lock();
        try {
            if (users[0]==null) {
                users[0]=user;
                playerHands.put(users[0], gameCardService.makeHand((short) 12, eviluser));
                bossCards.put(users[0], new BossCard(eviluser));
                eviluser=!eviluser;
                if (users[1]!=null) setRoomStatus(RoomStatus.GAME_PHASE);
                else setRoomStatus(RoomStatus.LOOKING_FOR_PEOPLE);
            }
            else {
                if (users[1]!=null) {
                    users[0]=user;
                    playerHands.put(users[0], gameCardService.makeHand((short) 12, eviluser));
                    bossCards.put(users[0], new BossCard(eviluser));
                    eviluser=!eviluser;
                    if (users[0]!=null) setRoomStatus(RoomStatus.GAME_PHASE);
                    else setRoomStatus(RoomStatus.LOOKING_FOR_PEOPLE);
                }
            }
        }
        catch(Throwable t) {
            LOGGER.warn(String.format("Exception during adding user into room.%n%s", t.toString()));
            rwLock.writeLock().unlock();
        }

    }

    @NotNull
    public UserHand getHand(PlayingUser user) {
        return new UserHand(this.playerHands.get(user), bossCards.get(user));
    }

    @NotNull
    public Set<Map.Entry<PlayingUser, Card[]>> getPlayerHands() {
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
    public boolean placeCard(PlayingUser user, Long cardId, int row, int place) {
        if (playerHands.containsKey(user)) {
            int i=0;
            boolean step =false;
            while (i<(int) this.cardsInHandByPlayer) {
                if (playerHands.get(user)[i]!= null && (int) playerHands.get(user)[i].getId()==cardId) {
                    step=true;
                    field.rows[row].putCardAt(place, playerHands.get(user)[i]);
                    playerHands.get(user)[i]=null;
                    i=(int) this.cardsInHandByPlayer;
                }
                i++;
            }
            return step;
        }
        else return false;
    }
    public void setField(GameField field1) {this.field=field1;}
    public GameField getCurrentField() {
        return field;
    }
    public boolean activateBossCard(PlayingUser user) {
        if (playerHands.containsKey(user)) {
            return bossCards.get(user).activate();
        }
        else return false;
    }

}
