package CardAugments.cardmods.rare;

import CardAugments.CardAugmentsMod;
import CardAugments.cardmods.AbstractAugment;
import basemod.abstracts.AbstractCardModifier;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;

public class JankMod extends AbstractAugment {
    public static final String ID = CardAugmentsMod.makeID(JankMod.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;

    public int upDamage = -1;
    public int upBlock = -1;
    public int upMagic = -1;

    @Override
    public void onInitialApplication(AbstractCard card) {
        boolean hasDamage = card.baseDamage > 0;
        boolean hasBlock = card.baseBlock > 0;
        int d = card.baseDamage;
        int b = card.baseBlock;
        int m = card.baseMagicNumber;
        if (hasDamage && hasBlock) {
            card.baseDamage = m;
            card.baseBlock = d;
            card.baseMagicNumber = b;
            card.magicNumber = card.baseMagicNumber;
        } else if (hasDamage) {
            card.baseDamage = m;
            card.baseMagicNumber = d;
            card.magicNumber = card.baseMagicNumber;
        } else {
            card.baseBlock = m;
            card.baseMagicNumber = b;
            card.magicNumber = card.baseMagicNumber;
        }
    }

    @Override
    public boolean validCard(AbstractCard card) {
        return (card.baseDamage > 0 || card.baseBlock > 0) && cardCheck(card, c -> doesntDowngradeMagic() && c.baseMagicNumber > 0);
    }

    @Override
    public void onUpgradeCheck(AbstractCard card) {
        super.onUpgradeCheck(card);
        boolean hasDamage = card.baseDamage > 0;
        boolean hasBlock = card.baseBlock > 0;
        if (hasDamage && hasBlock) {
            if (upDamage > 0) {
                card.baseBlock += upDamage;
                card.upgradedBlock = true;
            }
            if (upBlock > 0) {
                card.baseMagicNumber += upBlock;
                card.magicNumber = card.baseMagicNumber;
                card.upgradedMagicNumber = true;
            }
            if (upMagic > 0) {
                card.baseDamage += upMagic;
                card.upgradedBlock = true;
            }
        } else if (hasDamage) {
            if (upDamage > 0) {
                card.baseMagicNumber += upDamage;
                card.magicNumber = card.baseMagicNumber;
                card.upgradedMagicNumber = true;
            }
            if (upMagic > 0) {
                card.baseDamage += upMagic;
                card.upgradedDamage = true;
            }
        } else {
            if (upBlock > 0) {
                card.baseMagicNumber += upBlock;
                card.magicNumber = card.baseMagicNumber;
                card.upgradedMagicNumber = true;
            }
            if (upMagic > 0) {
                card.baseBlock += upMagic;
                card.upgradedBlock = true;
            }
        }
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
    public AugmentRarity getModRarity() {
        return AugmentRarity.RARE;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new JankMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}
