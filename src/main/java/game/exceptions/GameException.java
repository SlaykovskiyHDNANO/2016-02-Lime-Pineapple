package game.exceptions;

import org.jetbrains.annotations.NotNull;

/**
 * created: 6/4/2016
 * package: game.exceptions
 */
public class GameException extends Exception {

    public GameException(@NotNull String message) {
        super(message);
    }
}
