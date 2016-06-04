package game;

import game.GameRoom;
import game.PlayingUser;
import org.jetbrains.annotations.NotNull;

/**
 * created: 6/3/2016
 * package: game.services
 */
public interface CardEventHandler {

    void onPlace(@NotNull GameRoom room, @NotNull PlayingUser actor);
    void onBeforePlace(@NotNull GameRoom room, @NotNull PlayingUser actor);
    void onAfterPlace(@NotNull GameRoom room, @NotNull PlayingUser actor);
    void onBeforeRound(@NotNull GameRoom room, @NotNull PlayingUser actor);
    void onAfterRound(@NotNull GameRoom room, @NotNull PlayingUser actor);
}
