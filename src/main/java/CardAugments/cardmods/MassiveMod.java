package CardAugments.cardmods;

import CardAugments.CardAugmentsMod;
import basemod.abstracts.AbstractCardModifier;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;

public class MassiveMod extends AbstractAugment {
    public static final String ID = CardAugmentsMod.makeID("MassiveMod");
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;

    private static final float MULTI = 0.8f;

    @Override
    public void onInitialApplication(AbstractCard card) {
        super.onInitialApplication(card);
        if (card.baseDamage > 0) {
            card.baseDamage *= 1 + (MULTI/card.cost);
            card.damage = card.baseDamage;
        }
        if (card.baseBlock > 0) {
            card.baseBlock *= 1 + (MULTI/card.cost);
            card.block = card.baseBlock;
        }
        card.cost = card.cost + 1;
        card.costForTurn = card.cost;
    }

    @Override
    public boolean shouldApply(AbstractCard card) {
        AbstractCard upgradeCheck = card.makeCopy();
        upgradeCheck.upgrade();
        return card.cost == upgradeCheck.cost && card.cost > 0 && card.cost <= 3 && (card.baseDamage > 0 || card.baseBlock > 0);
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
    public AugmentRarity getModRarity() {
        return AugmentRarity.UNCOMMON;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new MassiveMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return super.identifier(card);
    }
}
