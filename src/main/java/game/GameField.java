package game;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonObjectFormatVisitor;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
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

    final Map<PlayingUser, Card> bosses;
    final Map<PlayingUser, GameFieldRow[]> playerToRows;


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
    public JsonObject serializeRoom() {
        final JsonObject jsonObject = new JsonObject();
        final JsonElement bossesArr = gson.toJsonTree(bosses.values().toArray());
        jsonObject.add("bosses", bossesArr);

        return jsonObject;
    }

}
