package db.models.game.cards;

import game.BossCard;
import game.Card;
import org.jetbrains.annotations.Nullable;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * created: 12-Mar-16
 * package: db.models.game.cards
 */
public class CardFactory {
    AtomicInteger count=new AtomicInteger(0);
    CardModel createModel(int num, boolean villain) {
        final CardModel model=new CardModel();
        if (villain) {
            model.addEffect(createEffect(num-10));
            switch (num) {
                case 1: {
                    model.cardName = "BOLOTNIK";
                    model.setCharacters(CardType.MONSTER, 10, true, false, true, false, 14);
                    return model;
                }
                case 2: {

                    model.cardName = "VOLKOLAK(LIDER)";
                    model.setCharacters(CardType.BEAST, 10, true, false, false, false, 8);
                    return model;
                }
                case 3: {

                    model.cardName = "BABA YAGA";
                    model.setCharacters(CardType.DAEMON, 10, true, false, true, false, 6);
                    return model;
                }
                case 4: {
                    model.cardName = "LIHO";
                    model.setCharacters(CardType.DAEMON, 10, true, false, false, false, 6);
                    return model;
                }
                case 5: {
                    model.cardName = "VOLKOLAK";
                    final boolean distant = new Random().nextBoolean();
                    model.setCharacters(CardType.BEAST, 10, !distant, false, distant, false, 6);
                    return model;
                }
                case 6: {
                    model.cardName = "VOLKOLAK (SHAMAN)";
                    model.setCharacters(CardType.BEAST, 10, false, false, true, false, 6);
                    return model;
                }
                case 7: {
                    model.cardName = "HEN AND CHICKEN";
                    model.setCharacters(CardType.BEAST, 10, false, true, false, true, 1);
                    return model;
                }
                case 8: {
                    model.cardName = "VOLKOLAK (BERSERK)";
                    model.setCharacters(CardType.BEAST, 10, true, false, false, false, 10);
                    return model;
                }
                case 9: {
                    model.cardName = "COSHEI";
                    model.setCharacters(CardType.DAEMON, 10, true, false, false, false, 5);
                    return model;
                }
                default: {
                    model.cardName = "TUGARIN";
                    model.setCharacters(CardType.DAEMON, 10, false, true, false, false, 10);
                    return model;
                }
            }
        }
        else {
            model.addEffect(createEffect(num));
            switch (num) {
                case 1: {
                    model.cardName = "DOMOVOI";
                    model.setCharacters(CardType.MONSTER, 10, false, false, true, true, 8);
                    return model;
                }
                case 2: {

                    model.cardName = "NESMEANA";
                    model.setCharacters(CardType.HUMAN, 10, true, true, false, false, 8);
                    return model;
                }
                case 3: {

                    model.cardName = "BOGATUR";
                    model.setCharacters(CardType.HUMAN, 10, true, false, false, false, 5);
                    return model;
                }
                case 4: {
                    model.cardName = "VARVARA";
                    model.setCharacters(CardType.HUMAN, 10, true, false, true, false, 6);
                    return model;
                }
                case 5: {
                    model.cardName = "FINIST";
                    final boolean distant = new Random().nextBoolean();
                    model.setCharacters(CardType.HUMAN, 10, true, false, true, false, 15);
                    return model;
                }
                case 6: {
                    model.cardName = "VASILISYA PREKRASNAYA";
                    model.setCharacters(CardType.HUMAN, 10, false, false, true, false, 7);
                    return model;
                }
                case 7: {
                    model.cardName = "MARIA MOREVNA";
                    model.setCharacters(CardType.HUMAN, 10, true, false, false, false, 12);
                    return model;
                }
                case 8: {
                    model.cardName = "MOROZKO";
                    model.setCharacters(CardType.HUMAN, 10, true, false, false, false, 18);
                    return model;
                }
                case 9: {
                    model.cardName = "LESOVIK";
                    model.setCharacters(CardType.BEAST, 10, false, true, false, false, 3);
                    return model;
                }
                default: {
                    model.cardName = "SESTRITSA ALENUSHKA BRATETS IVANUSHKA";
                    model.setCharacters(CardType.HUMAN, 10, true, false, false, false, 10);
                    return model;
                }
            }
        }

    }
    @Nullable
    EffectModel createEffect(int num) {
        switch (num) {
            case 1:
                return new EffectModel("Two kind cards added", "AddKindCards", CardType.MONSTER, "addcards.gif");
            case 2:
                return new EffectModel("Destroys enemys strongest card", "RemovwStringVickedCard", CardType.HUMAN, "remvickcard.gif");
            case 3:
                return new EffectModel("Pair Card","Pair card",CardType.HUMAN, "pair.gif" );
            case 5:
                return new EffectModel("Invulnarable to effects","Effect shield",CardType.HUMAN, "shield.gif" );
            case 6:
                return new EffectModel("ressurects killed card","Ressurect",CardType.HUMAN, "ressurect.gif" );
            case 8:
                return new EffectModel("Cold attack","Cold",CardType.HUMAN, "cold.gif" );
            case 9:
                return new EffectModel("Calls anotherCard","ALENUSHKACALL",CardType.BEAST, "alcall.gif" );
            case 11:
                return new EffectModel("Invulnarable to effects","Effect shield",CardType.MONSTER, "shield.gif" );
            case 12:
                return new EffectModel("Increases other cards strength 1.5 times","Strength increase",CardType.BEAST, "strength.gif" );
            case 13:
                return new EffectModel("Destroys any card","DestroyCard",CardType.DAEMON, "dagger.gif" );
            case 14:
                return new EffectModel("Calls two copys","Triple",CardType.DAEMON, "triple.gif" );
            case 16:
                return new EffectModel("Increases other cards strength 1.5 times","Strength increase",CardType.BEAST, "strength.gif" );
            case 17:
                return new EffectModel("Adds two evil cards","call of two evil cards",CardType.BEAST, "evilcall.gif" );
            case 18:
                return new EffectModel("Destroys enemy card","DestroyEnCard",CardType.BEAST, "dagger.gif" );
            case 19:
                return new EffectModel("Invulnarrable, sucks enemys power","SuckingStrength",CardType.DAEMON, "skull.gif" );
            default: return null;
        }

    }
    public Card createCard(int num, boolean vicked) {
        return new Card(createModel(num, vicked),count.incrementAndGet());
    }
}
