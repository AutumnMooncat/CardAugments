package CardAugments.cutStuff;

import CardAugments.CardAugmentsMod;
import CardAugments.cardmods.AbstractAugment;
import CardAugments.patches.UnrestrictedFieldPatches;
import basemod.abstracts.AbstractCardModifier;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;

public class UnrestrictedMod extends AbstractAugment {
    public static final String ID = CardAugmentsMod.makeID("UnrestrictedMod");
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;


    @Override
    public void onInitialApplication(AbstractCard card) {
        UnrestrictedFieldPatches.UnrestrictedField.unrestricted.set(card, true);
    }

    @Override
    public boolean validCard(AbstractCard card) {
        return card.cost > 0 && isNormalCard(card);
    }

    @Override
    public String modifyName(String cardName, AbstractCard card) {
        return TEXT[0] + cardName + TEXT[1];
    }

    @Override
    public String modifyDescription(String rawDescription, AbstractCard card) {
        return TEXT[2] + rawDescription;
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.RARE;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new UnrestrictedMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}
