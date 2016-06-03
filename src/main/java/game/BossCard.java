package game;

import db.models.game.cards.CardType;
import db.models.game.cards.EffectModel;

import java.util.Random;

/**
 * Created by igor on 03.06.16.
 */
public class BossCard {

    String name;
    boolean evil;
    EffectModel bossEffect;
    boolean activated;
    public BossCard(boolean vicked) {
        this.evil=vicked;
        if (evil) {
            name="LESHUI";
            bossEffect=new EffectModel("Calls any 2 card sum power 15", "Forest spiritism", CardType.DAEMON, "forestspiritism.gif");
            activated=false;
        }
        else {
            final boolean variant=new Random().nextBoolean();
            if (variant) {
                name="FINIST (BOSS)";
                bossEffect=new EffectModel("Burns any card other cards lose 1 power", "Burning", CardType.HUMAN, "burn.gif");

            }
            else {
                name="VASILISA PREMUDRAYA";
                bossEffect=new EffectModel("Show any 3 cards from Enemy's hands", "Wiseness", CardType.HUMAN, "wiseness.gif");
            }
            activated=false;
        }

    }
    public boolean activate() {
        if (!activated) {
            activated=true;
            return true;
        }
        else return false;
    }

}
