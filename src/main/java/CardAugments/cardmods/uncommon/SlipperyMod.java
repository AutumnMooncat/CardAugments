package CardAugments.cardmods.uncommon;

import CardAugments.CardAugmentsMod;
import CardAugments.cardmods.AbstractAugment;
import basemod.abstracts.AbstractCardModifier;
import com.megacrit.cardcrawl.actions.common.DiscardSpecificCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;

public class SlipperyMod extends AbstractAugment {
    public static final String ID = CardAugmentsMod.makeID("SlipperyMod");
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;

    @Override
    public void onInitialApplication(AbstractCard card) {
        card.isInnate = true;
    }

    @Override
    public boolean validCard(AbstractCard card) {
        return card.cost == -2;
    }

    @Override
    public void onDrawn(AbstractCard card) {
        this.addToBot(new DiscardSpecificCardAction(card));
    }

    @Override
    public String modifyName(String cardName, AbstractCard card) {
        return TEXT[0] + cardName + TEXT[1];
    }

    @Override
    public String modifyDescription(String rawDescription, AbstractCard card) {
        return rawDescription + TEXT[2];
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.UNCOMMON;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new SlipperyMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}
