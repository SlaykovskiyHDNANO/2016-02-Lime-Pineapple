package db.models.game.cards;

import org.jetbrains.annotations.NotNull;

/**
 * created: 12-Mar-16
 * package: db.models.game
 */
public enum CardType {
    NONE("Cards.NONE"),
    BOSS_CARD("Catds.Boss"),
    ONE_TIME_USE("Cards.Modifier"),
    HUMAN("Cards.Living.Humanoid.Human"),
    BEAST("Cards.Living.Creature.Beast"),
    MONSTER("Cards.Living.Creature.Monster"),
    DAEMON("Cards.Living.Spiritual.Daemon"),
    SPIRIT("Cards.Living.Spiritual.Spirit"),
    BOSS("Cards.Special");


    private final String cardName;
    /**
     * @param cardName Used to bind card name localized in desired language
     */
    CardType(final String cardName) {
        this.cardName = cardName;
    }

    /* (non-Javadoc)
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString() {
        return cardName;
    }

}
