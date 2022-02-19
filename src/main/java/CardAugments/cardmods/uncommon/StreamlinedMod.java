package CardAugments.cardmods.uncommon;

import CardAugments.CardAugmentsMod;
import CardAugments.cardmods.AbstractAugment;
import basemod.abstracts.AbstractCardModifier;
import com.megacrit.cardcrawl.actions.common.ReduceCostAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;

public class StreamlinedMod extends AbstractAugment {
    public static final String ID = CardAugmentsMod.makeID("StreamlinedMod");
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;

    @Override
    public void onInitialApplication(AbstractCard card) {
        card.cost = card.cost + 1;
        card.costForTurn = card.cost;
    }

    @Override
    public boolean shouldApply(AbstractCard card) {
        AbstractCard upgradeCheck = card.makeCopy();
        upgradeCheck.upgrade();
        return card.cost == upgradeCheck.cost && validCard(card);
    }

    @Override
    public boolean validCard(AbstractCard card) {
        return card.cost >= 1 && isNormalCard(card) && cardDoesntExhaust(card);
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
    public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
        addToBot(new ReduceCostAction(card.uuid, 1));
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.UNCOMMON;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new StreamlinedMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}
