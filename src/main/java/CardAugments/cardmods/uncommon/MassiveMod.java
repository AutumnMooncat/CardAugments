package CardAugments.cardmods.uncommon;

import CardAugments.CardAugmentsMod;
import CardAugments.cardmods.AbstractAugment;
import basemod.abstracts.AbstractCardModifier;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;

public class MassiveMod extends AbstractAugment {
    public static final String ID = CardAugmentsMod.makeID("MassiveMod");
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;

    @Override
    public void onInitialApplication(AbstractCard card) {
        super.onInitialApplication(card);
        if (card.baseDamage > 0) {
            card.baseDamage += getDamageBuff(card);
            card.damage = card.baseDamage;
        }
        if (card.baseBlock > 0) {
            card.baseBlock += getBlockBuff(card);
            card.block = card.baseBlock;
        }
        card.cost = card.cost + 1;
        card.costForTurn = card.cost;
    }

    public int getDamageBuff(AbstractCard card) {
        AbstractCard upgrade = card.makeCopy();
        card.upgrade();
        int check = Math.max(card.baseDamage, upgrade.baseDamage);
        if (check <= 3) {
            return 2;
        } else if (check <= 6) {
            return 3;
        } else if (check <= 9) {
            return 4;
        } else if (check <= 12) {
            return 5;
        } else {
            return 6;
        }
    }

    public int getBlockBuff(AbstractCard card) {
        AbstractCard upgrade = card.makeCopy();
        card.upgrade();
        int check = Math.max(card.baseBlock, upgrade.baseBlock);
        if (check <= 3) {
            return 2;
        } else if (check <= 6) {
            return 3;
        } else if (check <= 9) {
            return 4;
        } else if (check <= 12) {
            return 5;
        } else {
            return 6;
        }
    }

    @Override
    public boolean shouldApply(AbstractCard card) {
        AbstractCard upgradeCheck = card.makeCopy();
        upgradeCheck.upgrade();
        return card.cost == upgradeCheck.cost && validCard(card);
    }

    @Override
    public boolean validCard(AbstractCard card) {
        return card.cost > 0 && card.cost <= 3 && (card.baseDamage > 0 || card.baseBlock > 0);
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
        return new MassiveMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}
