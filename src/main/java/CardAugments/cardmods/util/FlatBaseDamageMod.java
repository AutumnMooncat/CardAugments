package CardAugments.cardmods.util;

import CardAugments.CardAugmentsMod;
import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import com.megacrit.cardcrawl.cards.AbstractCard;

public class FlatBaseDamageMod extends AbstractCardModifier {
    public static final String ID = CardAugmentsMod.makeID("FlatBaseDamageMod");
    int amount;

    public FlatBaseDamageMod(int amount) {
        this.amount = amount;
    }

    @Override
    public void onInitialApplication(AbstractCard card) {
        card.baseDamage += amount;
        card.damage = card.baseDamage;
    }

    @Override
    public boolean shouldApply(AbstractCard card) {
        if (CardModifierManager.hasModifier(card, identifier(card))) {
            FlatBaseDamageMod m = (FlatBaseDamageMod) CardModifierManager.getModifiers(card, identifier(card)).get(0);
            m.amount += amount;
            card.baseDamage += amount;
            card.damage = card.baseDamage;
            return false;
        }
        return true;
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new FlatBaseDamageMod(amount);
    }
}
