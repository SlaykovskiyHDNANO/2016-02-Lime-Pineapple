package db.models.game.cards;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.persistence.*;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

@Entity
@Table(name="CardModel")
//TODO: add events: OnCardPlaced(), OnCardExpired()
public class CardModel {

    @Id
    @Column
    public String cardName;

    @Column(name="power")
    int power;
    @Column(name="mellee")
    boolean playersCardContainerMelee=false;
    @Column(name="enemyMelee")
    boolean enemyCardContainerMelee=false;
    @Column(name="distant")
    boolean playerCardContainerDistant=false;
    @Column(name="enemyDistant")
    boolean enemyCardContainerDistant=false;
    @Column
    String cardDescription;
    @OneToMany()
    Collection<EffectModel> cardEffects;
    @Column(name="cardType")
    CardType cardType;

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

    public void setCharacters(CardType type, long lifetime, boolean playmelee, boolean enemymelee, boolean playdistant, boolean enemydistant, int strength) {
        this.cardType=type;
        this.cardEffects = new Vector<>();
        this.enemyCardContainerMelee=enemymelee;
        this.enemyCardContainerDistant=enemydistant;
        this.playerCardContainerDistant=playdistant;
        this.playersCardContainerMelee=playmelee;
        this.power=strength;
    }
}