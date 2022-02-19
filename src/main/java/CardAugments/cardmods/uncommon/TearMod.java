package CardAugments.cardmods.uncommon;

import CardAugments.CardAugmentsMod;
import CardAugments.actions.AndTearAction;
import CardAugments.cardmods.AbstractDynvarAugment;
import CardAugments.util.CalcHelper;
import basemod.abstracts.AbstractCardModifier;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;

public class TearMod extends AbstractDynvarAugment {
    public static final String ID = CardAugmentsMod.makeID("TearMod");
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;

    private static final int DAMAGE = 7;
    private static final int UPGRADE_DAMAGE = 9;

    @Override
    public void onInitialApplication(AbstractCard card) {
        super.onInitialApplication(card);
        baseValue = value = card.upgraded ? UPGRADE_DAMAGE : DAMAGE;
        card.cost = card.cost + 1;
        card.costForTurn = card.cost;
    }

    @Override
    public boolean shouldRenderValue() {
        return true;
    }

    @Override
    public boolean upgraded(AbstractCard card) {
        baseValue = value = card.upgraded ? UPGRADE_DAMAGE : DAMAGE;
        isValueModified = card.upgraded;
        return card.upgraded;
    }

    @Override
    public void updateDynvar(AbstractCard card) {
        baseValue = value = card.upgraded ? UPGRADE_DAMAGE : DAMAGE;
        isValueModified = false;
    }

    @Override
    public boolean shouldApply(AbstractCard card) {
        AbstractCard upgradeCheck = card.makeCopy();
        upgradeCheck.upgrade();
        return card.cost == upgradeCheck.cost && validCard(card);
    }

    @Override
    public void onApplyPowers(AbstractCard card) {
        baseValue = card.upgraded ? UPGRADE_DAMAGE : DAMAGE;
        value = CalcHelper.applyPowers(baseValue);
        isValueModified = value != baseValue;
    }

    @Override
    public boolean validCard(AbstractCard card) {
        return card.cost != -2 && isNormalCard(card);
    }

    @Override
    public String modifyName(String cardName, AbstractCard card) {
        return TEXT[0] + cardName + TEXT[1];
    }

    @Override
    public String modifyDescription(String rawDescription, AbstractCard card) {
        return rawDescription + String.format(TEXT[2], key);
    }

    @Override
    public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
        addToBot(new AndTearAction(baseValue));
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.UNCOMMON;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new TearMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}
