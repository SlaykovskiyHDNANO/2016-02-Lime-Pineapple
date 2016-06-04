package db.models.game.cards;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.persistence.*;
import java.util.Collection;
import java.util.Vector;

@Entity
@Table(name="CardModel")
//TODO: add events: OnCardPlaced(), OnCardExpired()
public class CardModel {

    @Id
    @Column
    public String cardName;

    @Column(name="power")
    int score;
    @Column(name="mellee")
    boolean playersCardContainerMelee=false;
    @Column(name="enemyMelee")
    boolean enemyCardContainerMelee=false;
    @Column(name="distant")
    boolean playerCardContainerDistant=false;
    @Column(name="enemyDistant")
    boolean enemyCardContainerDistant=false;
    @Column(name="playersContainerBoss")
    boolean playersContainerBoss = false;

    @Column
    String cardDescription;
    @OneToMany()
    Collection<EffectModel> cardEffects;
    @Column(name="cardType")
    CardType cardType;
    @Column(name="resourceType")
    String resourceType;

    public void setCardType(CardType type) {this.cardType=type;}

    @NotNull
    public CardType getCardType() {
        return cardType;
    }

    @NotNull
    public Collection<EffectModel> getCardEffects() {
        return cardEffects;
    }

    public CardModel() {
        this.cardEffects = new Vector<>();
        this.cardType = CardType.NONE;
    }
    public void addEffect(@Nullable EffectModel effect) {
        cardEffects.add(effect);
    }

    public void setCharacters(CardType type,
                              long lifetime,
                              boolean playmelee, boolean enemymelee, boolean playdistant, boolean enemydistant, int strength,
                              String resourcePath) {
        this.cardType=type;
        this.cardEffects = new Vector<>();
        this.enemyCardContainerMelee=enemymelee;
        this.enemyCardContainerDistant=enemydistant;
        this.playerCardContainerDistant=playdistant;
        this.playersCardContainerMelee=playmelee;
        this.playersContainerBoss = type == CardType.BOSS;
        this.score =strength;
    }

    public int getScore() {
        return score;
    }

    public boolean isPlayersCardContainerMelee() {
        return playersCardContainerMelee;
    }

    public boolean isEnemyCardContainerMelee() {
        return enemyCardContainerMelee;
    }

    public boolean isPlayerCardContainerDistant() {
        return playerCardContainerDistant;
    }

    public boolean isEnemyCardContainerDistant() {
        return enemyCardContainerDistant;
    }

    public boolean isPlayersContainerBoss() {
        return playersContainerBoss;
    }

    public String getCardDescription() {
        return cardDescription;
    }

    public String getResourceType() {
        return resourceType;
    }
}