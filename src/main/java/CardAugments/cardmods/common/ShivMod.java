package CardAugments.cardmods.common;

import CardAugments.CardAugmentsMod;
import CardAugments.cardmods.AbstractAugment;
import basemod.abstracts.AbstractCardModifier;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.tempCards.Shiv;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;

public class ShivMod extends AbstractAugment {
    public static final String ID = CardAugmentsMod.makeID("ShivMod");
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;

    private static final int SHIVS = 1;

    @Override
    public boolean validCard(AbstractCard card) {
        return card.cost >= 0 && card.type != AbstractCard.CardType.POWER && isNormalCard(card);
    }

    @Override
    public void onInitialApplication(AbstractCard card) {
        if (card.cardsToPreview == null) {
            card.cardsToPreview = new Shiv();
        }
    }

    @Override
    public String modifyName(String cardName, AbstractCard card) {
        return TEXT[0] + cardName + TEXT[1];
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
