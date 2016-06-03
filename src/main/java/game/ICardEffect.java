package game;

import db.models.game.cards.CardModel;
import game.GameRoom;
import game.PlayingUser;

import javax.validation.constraints.NotNull;

/**
 * created: 12-Mar-16
 * package: db.models.game
 */
public interface ICardEffect {

    @NotNull
    String getCardEffectName();

    @NotNull
    String getCardEffectDescription();

    @NotNull
    String getCardEffectType();

    void activateCardEffect(
                            @NotNull Card linkedCard,
                            @NotNull PlayingUser cardUser,
                            @NotNull GameRoom room );


}
