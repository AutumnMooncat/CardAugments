package CardAugments.cardmods.util;

import CardAugments.CardAugmentsMod;
import basemod.abstracts.AbstractCardModifier;
import com.megacrit.cardcrawl.cards.AbstractCard;

public class PreviewedMod extends AbstractCardModifier {
    public static final String ID = CardAugmentsMod.makeID(PreviewedMod.class.getSimpleName());
    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
    @Override
    public AbstractCardModifier makeCopy() {
        return new PreviewedMod();
    }
}
