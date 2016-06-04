package game;

import java.util.Map;
import java.util.Objects;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import game.exceptions.GameException;
import org.jetbrains.annotations.NotNull;


/**
 * created: 5/31/2016
 * package: game
 */
public class GameField {


    public enum FieldType {
        MELEE,
        RANGED
    }

    private static Gson gson = new Gson();

    Map<PlayingUser, Card> bosses;
    Map<PlayingUser, GameFieldRow[]> playerToRows;

    public static class TranslatedPosition {
        public final int rowIndex;
        public final PlayingUser user;

        TranslatedPosition(int rowIndex, PlayingUser user) {
            this.rowIndex = rowIndex;
            this.user = user;
        }
    }

    public GameField(
                          @NotNull Map<PlayingUser, GameFieldRow[]> rows,
                          @NotNull Map<PlayingUser, Card> bosses) {
        this.bosses = bosses;
        this.playerToRows = rows;
    }

    public Card getBoss(@NotNull PlayingUser user) {
        return bosses.get(user);
    }

    public Map<PlayingUser, GameFieldRow[]> getRows() {
        return this.playerToRows;
    }

    public GameFieldRow[] getRow(@NotNull PlayingUser user) {
        return this.playerToRows.get(user);
    }

    @NotNull
    protected PlayingUser getAnotherPlayer(@NotNull PlayingUser thisPlayer) throws GameException {
        for (PlayingUser user : this.playerToRows.keySet()) {
            if (!Objects.equals(user, thisPlayer)) {
                return user;
            }
        }
        throw new GameException("Could not find another user. Every user in room equals this user!");
    }

    public TranslatedPosition translateRowIndexToRowOwner(@NotNull  PlayingUser userPerspective, int rowIndex) throws GameException {
        final int rowsPerUser = this.playerToRows.get(userPerspective).length;
        if (rowIndex >= rowsPerUser) {
            return new TranslatedPosition(rowIndex % rowsPerUser, userPerspective);
        } else {

            return new TranslatedPosition( (rowIndex + 1) % rowsPerUser, this.getAnotherPlayer(userPerspective));
        }
    }

    public void putCard( @NotNull Card card, @NotNull TranslatedPosition position,  @NotNull Integer columnIndex) {
        this.playerToRows.get(position.user)[position.rowIndex].putCardAt(columnIndex, card);
    }

    /*@NotNull
    public JsonObject serializeRoom() {
        final JsonObject jsonObject = new JsonObject();
        final JsonElement bossesArr = gson.toJsonTree(bosses.values().toArray());
        jsonObject.add("bosses", bossesArr);

        return jsonObject;
    }*/

}
