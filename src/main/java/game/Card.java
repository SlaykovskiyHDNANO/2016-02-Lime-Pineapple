package game;

import db.models.game.cards.CardModel;
import org.jetbrains.annotations.NotNull;

/**
 * created: 6/1/2016
 * package: game
 */
public class Card {

    private final CardModel model;
    private final Integer id;

    @NotNull
    public CardModel getModel() {
        return model;
    }

    @NotNull
    public Integer getId() {
        return id;
    }

    public Card(@NotNull  CardModel model, @NotNull  Integer id) {
        this.model = model;
        this.id = id;
    }
}
