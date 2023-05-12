package CardAugments.cardmods.uncommon;

import CardAugments.CardAugmentsMod;
import CardAugments.cardmods.AbstractAugment;
import basemod.abstracts.AbstractCardModifier;
import basemod.patches.com.megacrit.cardcrawl.cards.AbstractCard.MultiCardPreview;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDrawPileAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.status.VoidCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;

public class VoidMod extends AbstractAugment {
    public static final String ID = CardAugmentsMod.makeID(VoidMod.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;
    public static final String[] CARD_TEXT = CardCrawlGame.languagePack.getUIString(ID).EXTRA_TEXT;

    private static final int VOIDS = 1;

    @Override
    public void onInitialApplication(AbstractCard card) {
        MultiCardPreview.add(card, new VoidCard());
        card.cost -= 2;
        if (card.cost < 0) {
            card.cost = 0;
        }
        card.costForTurn = card.cost;
    }

    @Override
    public boolean validCard(AbstractCard card) {
        return card.cost >= 2 && cardCheck(card, c -> doesntUpgradeCost());
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
        return insertAfterText(rawDescription , String.format(CARD_TEXT[0], VOIDS));
    }

    @Override
    public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
        this.addToBot(new MakeTempCardInDrawPileAction(new VoidCard(), VOIDS, true, true));
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.UNCOMMON;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new VoidMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}
