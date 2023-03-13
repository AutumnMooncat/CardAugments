package CardAugments.cardmods.uncommon;

import CardAugments.CardAugmentsMod;
import CardAugments.cardmods.AbstractAugment;
import basemod.abstracts.AbstractCardModifier;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;

public class BloodyMod extends AbstractAugment {
    public static final String ID = CardAugmentsMod.makeID(BloodyMod.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;

    @Override
    public void onInitialApplication(AbstractCard card) {
        card.cost = card.cost + 1;
        card.costForTurn = card.cost;
    }

    @Override
    public void onDamaged(AbstractCard c) {
        c.updateCost(-1);
    }

    @Override
    public boolean validCard(AbstractCard card) {
        return card.cost >= 1 && cardCheck(card, c -> doesntUpgradeCost());
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
    public String modifyDescription(String rawDescription, AbstractCard card) {
        if (rawDescription.contains(TEXT[4])) {
            return rawDescription.replace(TEXT[4], TEXT[5]);
        }
        return TEXT[2] + rawDescription;
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.UNCOMMON;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new BloodyMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}
