package game;

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



    final GameFieldRow[] rows; //current game deck
    public GameField(GameFieldRow[] rows) {
        this.rows = rows;
    }
}
