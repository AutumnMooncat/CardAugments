package CardAugments.cardmods.common;

import CardAugments.CardAugmentsMod;
import CardAugments.cardmods.AbstractAugment;
import CardAugments.dynvars.ModVar;
import CardAugments.util.CalcHelper;
import basemod.abstracts.AbstractCardModifier;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.NextTurnBlockPower;

public class RollMod extends AbstractAugment {
    public static final String ID = CardAugmentsMod.makeID("RollMod");
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;

    private static final int BLOCK = 4;
    private static final int UPGRADE_BLOCK = 6;

    public int getAmount(AbstractCard card) {
        return card.upgraded ? UPGRADE_BLOCK : BLOCK;
    }

    @Override
    public void onInitialApplication(AbstractCard card) {
        super.onInitialApplication(card);
        ModVar.setVal(card, getAmount(card));
        ModVar.setBaseVal(card, getAmount(card));
        modifyBaseStat(card, BuffType.BLOCK, BuffScale.MINOR_DEBUFF);
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
    public void onApplyPowers(AbstractCard card) {
        ModVar.setBaseVal(card, getAmount(card));
        ModVar.setVal(card, CalcHelper.applyPowersToBlock(getAmount(card)));
        ModVar.updateModified(card);
    }


    @Override
    public boolean validCard(AbstractCard card) {
        return card.cost != -2 &&  card.baseBlock > 1;
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
        this.addToBot(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new NextTurnBlockPower(AbstractDungeon.player, ModVar.getVal(card))));
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.COMMON;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new RollMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}
