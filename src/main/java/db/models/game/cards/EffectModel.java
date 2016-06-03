package db.models.game.cards;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by igor on 29.05.16.
 */
@Entity
@Table(name="Effect")
public class EffectModel {

    @Id
    @Column(name="effectName")
    @NotNull
    final String name;

    @Column(name="effectPath")
    @Nullable
    final String visualEffect;

    @Column(name="desciption")
    @NotNull
    final String description;

    @Column(name="effectType")
    final CardType typeName;


    EffectModel() {
        this.name = "";
        this.visualEffect = "";
        this.description = "";
        this.typeName = CardType.NONE;
    }

    public EffectModel(@NotNull String descriptions, @NotNull String cardName, @NotNull CardType type, @NotNull String effectPath) {
        description=descriptions;
        name=cardName;
        typeName = type;
        visualEffect=effectPath;
    }
}
