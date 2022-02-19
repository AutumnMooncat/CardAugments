package CardAugments.cardmods.uncommon;

import CardAugments.CardAugmentsMod;
import CardAugments.actions.AndTearAction;
import CardAugments.cardmods.AbstractAugment;
import CardAugments.patches.ModVar;
import CardAugments.util.CalcHelper;
import basemod.abstracts.AbstractCardModifier;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;

public class TearMod extends AbstractAugment {
    public static final String ID = CardAugmentsMod.makeID("TearMod");
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;

    private static final int DAMAGE = 7;
    private static final int UPGRADE_DAMAGE = 9;

    @Override
    public void onInitialApplication(AbstractCard card) {
        super.onInitialApplication(card);
        ModVar.setVal(card, getAmount(card));
        ModVar.setBaseVal(card, getAmount(card));
        modifyBaseStat(card, BuffType.DAMAGE, BuffScale.MAJOR_DEBUFF);
        modifyBaseStat(card, BuffType.BLOCK, BuffScale.MODERATE_DEBUFF);
    }

    @Override
    public void onUpgradeCheck(AbstractCard card) {
        ModVar.setVal(card, getAmount(card));
        ModVar.setBaseVal(card, getAmount(card));
        ModVar.setModified(card, card.upgraded);
        ModVar.setUpgraded(card, card.upgraded);
    }

    @Override
    public void updateDynvar(AbstractCard card) {
        ModVar.setVal(card, getAmount(card));
        ModVar.setBaseVal(card, getAmount(card));
        ModVar.setModified(card, false);
    }

    @Override
    public boolean shouldApply(AbstractCard card) {
        AbstractCard upgradeCheck = card.makeCopy();
        upgradeCheck.upgrade();
        return card.cost == upgradeCheck.cost && validCard(card);
    }

    @Override
    public void onApplyPowers(AbstractCard card) {
        ModVar.setBaseVal(card, getAmount(card));
        ModVar.setVal(card, CalcHelper.applyPowers(getAmount(card)));
        ModVar.updateModified(card);
    }

    @Override
    public boolean validCard(AbstractCard card) {
        return card.cost != -2 && isNormalCard(card) && (card.baseDamage > 1 || card.baseBlock > 1);
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
    public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
        addToBot(new AndTearAction(ModVar.getBaseVal(card)));
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.RARE;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new TearMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }

    public int getAmount(AbstractCard card) {
        return card.upgraded ? UPGRADE_DAMAGE : DAMAGE;
    }
}
