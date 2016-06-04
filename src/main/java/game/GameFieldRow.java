package game;

import db.models.game.cards.CardModel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * created: 6/1/2016
 * package: game
 */
public class GameFieldRow {
    final List<Card> row = Collections.synchronizedList(new ArrayList<>());
    final GameField.FieldType type;

    public GameFieldRow(GameField.FieldType type) {
        this.type = type;
    }

    void putCardAt(int position, @NotNull Card card) {
        this.row.toArray()[position] = card;
    }

    @Nullable
    Card getCardAt(int position) {
        return (Card) row.toArray()[position];
    }

    boolean hasCardAt(int position) {
        return row.toArray()[position] != null;
    }

    @NotNull
    Card[] getRow() {
        return (Card[]) this.row.toArray();
    }

    GameField.FieldType getType() {
        return this.type;
    }
}
