package game;

import db.models.game.cards.CardModel;
import org.jetbrains.annotations.NotNull;

/**
 * created: 6/1/2016
 * package: game
 */
public class Card {

    final CardModel model;
    final PlayingUser owner;
    final Integer id;

    @NotNull
    public CardModel getModel() {
        return model;
    }

    @NotNull
    public Integer getId() {
        return id;
    }

    public PlayingUser getOwner() {
        return owner;
    }

    public Card(@NotNull  CardModel model, @NotNull  Integer id, @NotNull PlayingUser owner) {
        this.model = model;
        this.id = id;
        this.owner = owner;
    }


}
