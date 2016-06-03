package game;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
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
    Map<PlayingUser, BossCard> bosses=new ConcurrentHashMap<>();
    final GameFieldRow[] rows; //current game deck
    public GameField(GameFieldRow[] rows) {
        this.rows = rows;
    }
    public void setBoss(PlayingUser user, BossCard card){
        bosses.put(user, card);
    }
    public BossCard getBoss(PlayingUser user) {
        return bosses.get(user);
    }
}
