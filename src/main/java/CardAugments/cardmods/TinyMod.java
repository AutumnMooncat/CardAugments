package CardAugments.cardmods;

import CardAugments.CardAugmentsMod;
import basemod.abstracts.AbstractCardModifier;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;

public class TinyMod extends AbstractAugment {
    public static final String ID = CardAugmentsMod.makeID("TinyMod");
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;

    @Override
    public void onInitialApplication(AbstractCard card) {
        super.onInitialApplication(card);
        if (card.baseDamage > 1) {
            card.baseDamage -= getDamageNerf(card);
            card.damage = card.baseDamage;
        }
        if (card.baseBlock > 1) {
            card.baseBlock -= getBlockNerf(card);
            card.block = card.baseBlock;
        }
        card.cost = card.cost - 1;
        card.costForTurn = card.cost;
    }

    public int getDamageNerf(AbstractCard card) {
        AbstractCard upgrade = card.makeCopy();
        card.upgrade();
        int check = Math.max(card.baseDamage, upgrade.baseDamage);
        if (check <= 3) {
            return 1;
        } else if (check <= 6) {
            return 2;
        } else if (check <= 9) {
            return 3;
        } else if (check <= 12) {
            return 4;
        } else {
            return 5;
        }
    }

    public int getBlockNerf(AbstractCard card) {
        AbstractCard upgrade = card.makeCopy();
        card.upgrade();
        int check = Math.max(card.baseBlock, upgrade.baseBlock);
        if (check <= 3) {
            return 1;
        } else if (check <= 6) {
            return 2;
        } else if (check <= 9) {
            return 3;
        } else if (check <= 12) {
            return 4;
        } else {
            return 5;
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
