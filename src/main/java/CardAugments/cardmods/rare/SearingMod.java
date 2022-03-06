package CardAugments.cardmods.rare;

import CardAugments.CardAugmentsMod;
import CardAugments.cardmods.AbstractAugment;
import CardAugments.patches.InfiniteUpgradesPatches;
import basemod.abstracts.AbstractCardModifier;
import com.evacipated.cardcrawl.mod.stslib.cards.interfaces.BranchingUpgradesCard;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;

public class SearingMod extends AbstractAugment {
    public static final String ID = CardAugmentsMod.makeID("SearingMod");
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;

    @Override
    public void onInitialApplication(AbstractCard card) {
        InfiniteUpgradesPatches.InfUpgradeField.inf.set(card, true);
    }

    @Override
    public boolean canRoll(AbstractCard card) {
        AbstractCard base = card.makeCopy();
        AbstractCard upgradeCheck = card.makeCopy();
        upgradeCheck.upgrade();
        return validCard(card) && ((base.baseMagicNumber < upgradeCheck.baseMagicNumber && usesMagic(upgradeCheck)) || base.baseDamage < upgradeCheck.baseDamage || base.baseBlock < upgradeCheck.baseBlock);
    }

    @Override
    public boolean validCard(AbstractCard card) {
        return isNormalCard(card) && doesntOverride(card, "canUpgrade") && !(card instanceof BranchingUpgradesCard);
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
        return new SearingMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}
