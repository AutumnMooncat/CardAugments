package CardAugments.cardmods.util;

import CardAugments.CardAugmentsMod;
import CardAugments.cardmods.AbstractAugment;
import CardAugments.patches.InterruptUseCardFieldPatches;
import basemod.abstracts.AbstractCardModifier;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class OmegaMod extends AbstractAugment {
    public static final String ID = CardAugmentsMod.makeID(OmegaMod.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;
    private static final float multiplier = 3f;

    boolean modMagic;

    @Override
    public void onInitialApplication(AbstractCard card) {
        if (cardCheck(card, c -> doesntDowngradeMagic())) {
            modMagic = true;
        }
        InterruptUseCardFieldPatches.InterceptUseField.interceptUse.set(card, false);
    }

    @Override
    public float modifyBaseDamage(float damage, DamageInfo.DamageType type, AbstractCard card, AbstractMonster target) {
        if (card.baseDamage > 0) {
            return damage * multiplier;
        }
        return damage;
    }

    @Override
    public float modifyBaseBlock(float block, AbstractCard card) {
        if (card.baseBlock > 0) {
            return block * multiplier;
        }
        return block;
    }

    @Override
    public float modifyBaseMagic(float magic, AbstractCard card) {
        if (modMagic) {
            return magic * multiplier;
        }
        return magic;
    }

    @Override
    public boolean validCard(AbstractCard card) {
        return noShenanigans(card) && cardCheck(card, c -> c.cost >= 2 && doesntUpgradeCost() && (c.baseDamage > 0 || c.baseBlock > 0 || doesntDowngradeMagic()));
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
        return AugmentRarity.SPECIAL;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new OmegaMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }

}
