package CardAugments.cardmods.uncommon;

import CardAugments.CardAugmentsMod;
import CardAugments.cardmods.AbstractAugment;
import basemod.abstracts.AbstractCardModifier;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;

public class TinyMod extends AbstractAugment {
    public static final String ID = CardAugmentsMod.makeID("TinyMod");
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;

    @Override
    public void onInitialApplication(AbstractCard card) {
        if (card.baseDamage > 1) {
            modifyBaseStat(card, BuffType.DAMAGE, BuffScale.MODERATE_DEBUFF);
        }
        if (card.baseBlock > 1) {
            modifyBaseStat(card, BuffType.BLOCK, BuffScale.MODERATE_DEBUFF);
        }
        card.cost = card.cost - 1;
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
        return card.cost > 0 && card.cost <= 3 && (card.baseDamage > 1 || card.baseBlock > 1);
    }

    @Override
    public String modifyName(String cardName, AbstractCard card) {
        return TEXT[0] + cardName + TEXT[1];
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.UNCOMMON;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new TinyMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}
