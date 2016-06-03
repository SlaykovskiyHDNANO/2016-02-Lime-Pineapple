package game;

import db.models.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import server.rest.UserServlet;

/**
 * created: 12-Mar-16
 * package: db.models.game
 */

// This class describes user state during game
public class PlayingUser {
    private User linkedUser;
    private int currentScore = 0;
    private long currentRoom;
    private short lives = 0;

    @NotNull
    public User getLinkedUser() {
        return linkedUser;
    }
    @NotNull
    public Integer getCurrentScore() {
        return currentScore;
    }

    public void setCurrentScore(@NotNull Integer score) {
        this.currentScore = score;
    }

    public void incrementScore(@NotNull Integer delta) {
        this.currentScore += delta;
    }


    public PlayingUser(@NotNull  User user) {
        this.linkedUser = user;
    }


    public String getName() {
        return linkedUser.getUsername();
    }
    public void setCurrentRoom(long roomId) {
        this.currentRoom=roomId;
    }
    public long getCurrentRoom() {
        return currentRoom;
    }
}
