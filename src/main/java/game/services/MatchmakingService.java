package game.services;

import game.GameRoom;
import game.PlayingUser;
import org.jetbrains.annotations.NotNull;
import server.messaging.MessageService;
import server.messaging.messages.SystemMessage;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

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
    }
    final Map<Long, GameRoom> activeRooms = new ConcurrentHashMap<>();
    final AtomicLong counter = new AtomicLong(0L);
    final AtomicLong userCounter=new AtomicLong(0L);
    final Map<Long, PlayingUser> activeUsers = new ConcurrentHashMap<>();
    final MessageService service;
    PlayingUser waitingUser=null;
    final GameCardService gameCardService=new GameCardService();
    public MatchmakingService(@NotNull MessageService service) {
        this.service = service;
    }

    public void configure() {
        this.service.subscribe(SystemMessage.MESSAGES.CLIENT_DISCONNECTED, (sender, message) -> {
            // TODO: do something here
            sender.getUser();

        });

        this.service.subscribe(MESSAGES.CREATE_ROOM_REQUEST, (sender, message) -> {
            // TODO: do something here
            final long newRoomId=counter.incrementAndGet();
            final GameRoom newGame=new GameRoom(gameCardService, newRoomId, (short)10);
            service.addActiveRoom(newGame);
        });

        this.service.subscribe(MESSAGES.LEAVE_ROOM_REQUEST, (sender, message) -> {
            // TODO: do something here
            service.removeClient(sender);

        });

        this.service.subscribe(MESSAGES.JOIN_ROOM_REQUEST, (sender, message) -> {
            // TODO: do something here
            if (waitingUser==null) waitingUser=sender.getUser();
            else {
                final long newRoomId=counter.incrementAndGet();
                final GameRoom gameRoom=new GameRoom(gameCardService,newRoomId, (short)12);
                waitingUser.setCurrentRoom(newRoomId);
                sender.getUser().setCurrentRoom(newRoomId);
                gameRoom.addUser(waitingUser);
                gameRoom.addUser(sender.getUser());
                service.addActiveRoom(gameRoom);
                waitingUser=null;
            }
        });

        this.service.subscribe(MESSAGES.LIST_ROOMS_REQUEST, (sender, message) -> {
            // TODO: do something here

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
        return user.getCurrentRoom();
    }

    @Nullable
    public PlayingUser getUserOpponent(PlayingUser user) {
        if (user.getCurrentRoom()==-1L) return null;
        final GameRoom userRoom=activeRooms.get(user.getCurrentRoom());
        return userRoom.getOpponent(user);
    }
    public void userExitedRoom(PlayingUser user) {
        if (user.getCurrentRoom()!=-1L) {
            activeRooms.get(user.getCurrentRoom()).userExited(user);
            user.setRoom(-1L);
        }
    }*/
}
