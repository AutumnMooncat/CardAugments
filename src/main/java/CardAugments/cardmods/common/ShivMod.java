package CardAugments.cardmods.common;

import CardAugments.CardAugmentsMod;
import CardAugments.cardmods.AbstractAugment;
import CardAugments.patches.MultiPreviewFieldPatches;
import basemod.abstracts.AbstractCardModifier;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.tempCards.Miracle;
import com.megacrit.cardcrawl.cards.tempCards.Shiv;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;

public class ShivMod extends AbstractAugment {
    public static final String ID = CardAugmentsMod.makeID("ShivMod");
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;

    private static final int SHIVS = 1;

    @Override
    public boolean validCard(AbstractCard card) {
        return card.cost != -2 && (card.baseBlock > 1 || card.cardsToPreview instanceof Shiv);
    }

    @Override
    public void onInitialApplication(AbstractCard card) {
        if (card.baseBlock > 1) {
            modifyBaseStat(card, BuffType.BLOCK, BuffScale.MINOR_DEBUFF);
        }
        MultiPreviewFieldPatches.addPreview(card, new Shiv());
    }

    @Override
    public String modifyName(String cardName, AbstractCard card) {
        String[] nameParts = removeUpgradeText(cardName);
        return TEXT[0] + nameParts[0] + TEXT[1] + nameParts[1];
    }

    @Override
    public String modifyDescription(String rawDescription, AbstractCard card) {
        return rawDescription + String.format(TEXT[2], SHIVS);
    }

    @Override
    public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
        this.addToBot(new MakeTempCardInHandAction(new Shiv(), SHIVS));
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.COMMON;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new ShivMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}
