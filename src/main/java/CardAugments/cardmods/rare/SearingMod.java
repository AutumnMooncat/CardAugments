package CardAugments.cardmods.rare;

import CardAugments.CardAugmentsMod;
import CardAugments.cardmods.AbstractAugment;
import CardAugments.patches.InfiniteUpgradesPatches;
import basemod.abstracts.AbstractCardModifier;
import com.evacipated.cardcrawl.mod.stslib.cards.interfaces.BranchingUpgradesCard;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;

public class SearingMod extends AbstractAugment {
    public static final String ID = CardAugmentsMod.makeID(SearingMod.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;

    @Override
    public void onInitialApplication(AbstractCard card) {
        InfiniteUpgradesPatches.InfUpgradeField.inf.set(card, true);
    }

    @Override
    public boolean validCard(AbstractCard card) {
        return isNormalCard(card) && card.canUpgrade() && cardCheck(card, c -> upgradesAVariable()) && doesntOverride(card, "canUpgrade") && !(card instanceof BranchingUpgradesCard);
    }

    @Override
    public String getPrefix() {
        return TEXT[0];
    }

    @Override
    public String getSufix() {
        return TEXT[1];
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
