package game;

import db.models.User;
import org.jetbrains.annotations.NotNull;

/**
 * created: 12-Mar-16
 * package: db.models.game
 */

// This class describes user state during game
public class PlayingUser {
    private User linkedUser;
    private int currentScore = 0;
    private long currentRoomId;
    private boolean skipped=false;

    public boolean isSkipped() {
        return skipped;
    }

    public void setSkipped(boolean skipped) {
        this.skipped = skipped;
    }

    public short getLives() {
        return lives;
    }

    public void setLives(short lives) {
        this.lives = lives;
    }

    public void setCurrentScore(int currentScore) {
        this.currentScore = currentScore;
    }

    private short lives = 2;

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
    public void setCurrentRoomId(long roomId) {
        this.currentRoomId =roomId;
    }
    public long getCurrentRoomId() {
        return currentRoomId;
    }
}
