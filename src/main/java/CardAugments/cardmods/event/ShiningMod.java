package CardAugments.cardmods.event;

import CardAugments.CardAugmentsMod;
import CardAugments.cardmods.AbstractAugment;
import CardAugments.util.Wiz;
import basemod.abstracts.AbstractCardModifier;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;

public class ShiningMod extends AbstractAugment {
    public static final String ID = CardAugmentsMod.makeID(ShiningMod.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;
    public static final String[] CARD_TEXT = CardCrawlGame.languagePack.getUIString(ID).EXTRA_TEXT;

    @Override
    public void onInitialApplication(AbstractCard card) {
        card.isEthereal = true;
    }

    @Override
    public boolean validCard(AbstractCard card) {
        return isNormalCard(card) && cardCheck(card, AbstractAugment::notEthereal);
    }

    @Override
    public void onDrawn(AbstractCard card) {
        card.superFlash();
        Wiz.atb(new GainEnergyAction(1));
    }

    @Override
    public String getPrefix() {
        return TEXT[0];
    }

    @Override
    public String getSuffix() {
        return TEXT[1];
    }

    @Override
    public String getAugmentDescription() {
        return TEXT[2];
    }

    @Override
    public String modifyDescription(String rawDescription, AbstractCard card) {
        return CARD_TEXT[0] + insertAfterText(rawDescription , CARD_TEXT[1]);
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.SPECIAL;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new ShiningMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }

}
