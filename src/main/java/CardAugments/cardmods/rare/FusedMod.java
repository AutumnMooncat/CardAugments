package CardAugments.cardmods.rare;

import CardAugments.CardAugmentsMod;
import CardAugments.cardmods.AbstractAugment;
import CardAugments.patches.CantUpgradeFieldPatches;
import basemod.abstracts.AbstractCardModifier;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.colorless.PanicButton;
import com.megacrit.cardcrawl.core.CardCrawlGame;

public class FusedMod extends AbstractAugment {
    public static final String ID = CardAugmentsMod.makeID("FusedMod");
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;

    @Override
    public void onInitialApplication(AbstractCard card) {
        CantUpgradeFieldPatches.CantUpgradeField.preventUpgrades.set(card, true);
        AbstractCard upgradeCheck = card.makeCopy();
        upgradeCheck.upgrade();
        if (card.baseDamage > 0) {
            modifyBaseStat(card, BuffType.DAMAGE, BuffScale.MAJOR_BUFF);
        }
        if (card.baseBlock > 0) {
            modifyBaseStat(card, BuffType.BLOCK, BuffScale.MAJOR_BUFF);
        }
        if (usesMagic(card) && card.baseMagicNumber <= upgradeCheck.baseMagicNumber && !(card instanceof PanicButton)) {
            modifyBaseStat(card, BuffType.MAGIC, BuffScale.MAJOR_BUFF);
        }
    }

    @Override
    public boolean canRoll(AbstractCard card) {
        AbstractCard upgradeCheck = card.makeCopy();
        while (card.timesUpgraded >= upgradeCheck.timesUpgraded) {
            upgradeCheck.upgraded = false;
            upgradeCheck.upgrade();
        }
        return validCard(card) && ((card.baseMagicNumber < upgradeCheck.baseMagicNumber && usesMagic(upgradeCheck) && usesMagic(card)) || card.baseDamage < upgradeCheck.baseDamage || card.baseBlock < upgradeCheck.baseBlock);
    }

    @Override
    public boolean validCard(AbstractCard card) {
        try {
            return card.canUpgrade() && !card.upgraded && card.getClass().getMethod("canUpgrade").getDeclaringClass().equals(AbstractCard.class);
        } catch (NoSuchMethodException ignored) {}
        return false;
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
    public AugmentRarity getModRarity() {
        return AugmentRarity.RARE;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new FusedMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}
