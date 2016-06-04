package server.messaging;

import game.GameRoom;
import org.jetbrains.annotations.NotNull;

/**
 * created: 6/4/2016
 * package: server.messaging
 */
public interface OnNewActiveRoomCallback {
    void run(@NotNull GameRoom room);
}
