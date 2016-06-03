package server.messaging;

import game.PlayingUser;
import org.jetbrains.annotations.NotNull;

/**
 * created: 5/25/2016
 * package: server.messaging
 */
public class Client {
    private final PlayingUser user;
    private long clientId = 0;

    public Client(PlayingUser user) {
        this.user = user;
    }



    public PlayingUser getUser() {
        return user;
    }

    public void setClientId(long id) {
        this.clientId = id;
    }

    public long getClientId() {
        return clientId;
    }
}
