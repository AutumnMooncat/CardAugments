package CardAugments.cardmods;

import CardAugments.CardAugmentsMod;
import CardAugments.patches.UnrestrictedFieldPatches;
import basemod.abstracts.AbstractCardModifier;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;

public class UnrestrictedMod extends AbstractAugment {
    public static final String ID = CardAugmentsMod.makeID("UnrestrictedMod");
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;


    @Override
    public void onInitialApplication(AbstractCard card) {
        super.onInitialApplication(card);
        UnrestrictedFieldPatches.UnrestrictedField.unrestricted.set(card, true);
    }

    @Override
    public boolean validCard(AbstractCard card) {
        return card.cost > 0 && isNormalCard(card);
    }

    @Override
    public String getPrefix() {
        return TEXT[1];
    }

    @Override
    public String getSuffix() {
        return TEXT[2];
    }

    @Override
    public String modifyDescription(String rawDescription, AbstractCard card) {
        return TEXT[0] + rawDescription;
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
        return super.identifier(card);
    }
}
