package CardAugments.cardmods.uncommon;

import CardAugments.CardAugmentsMod;
import CardAugments.cardmods.AbstractAugment;
import CardAugments.cardmods.DynvarCarrier;
import basemod.abstracts.AbstractCardModifier;
import com.megacrit.cardcrawl.actions.common.ModifyBlockAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;

public class BracedMod extends AbstractAugment implements DynvarCarrier {
    public static final String ID = CardAugmentsMod.makeID(BracedMod.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;
    public static final String[] CARD_TEXT = CardCrawlGame.languagePack.getUIString(ID).EXTRA_TEXT;
    private static final String KEY = "!" + ID + "!";
    public int baseAmount;
    public int upAmount;
    public boolean modified;
    public boolean upgraded;

    public int getBaseVal(AbstractCard card) {
        return baseAmount + getEffectiveUpgrades(card) * upAmount;
    }

    @Override
    public float modifyBaseBlock(float block, AbstractCard card) {
        return block - baseAmount;
    }

    @Override
    public void onInitialApplication(AbstractCard card) {
        AbstractCard base = makeNewInstance(card);
        if (base != null) {
            baseAmount = (int) Math.ceil(base.baseBlock * (1 - MAJOR_DEBUFF));
            base.upgrade();
            upAmount = (int) Math.ceil(base.baseBlock * (1 - MAJOR_DEBUFF)) - baseAmount;
        }
    }

    @Override
    public boolean validCard(AbstractCard card) {
        return card.cost != -2 && card.baseBlock >= 3 && makeNewInstance(card) != null && cardCheck(card, c -> doesntExhaust(c));
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
    public String modifyDescription(String rawDescription, AbstractCard card) {
        return insertAfterText(rawDescription, String.format(CARD_TEXT[0], KEY));
    }

    @Override
    public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
        this.addToBot(new ModifyBlockAction(card.uuid, getBaseVal(card)));
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.UNCOMMON;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new BracedMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }

    @Override
    public String key() {
        return ID;
    }

    @Override
    public int val(AbstractCard card) {
        return getBaseVal(card);
    }

    @Override
    public int baseVal(AbstractCard card) {
        return getBaseVal(card);
    }

    @Override
    public boolean modified(AbstractCard card) {
        return modified;
    }

    @Override
    public boolean upgraded(AbstractCard card) {
        if (upAmount == 0) {
            return false;
        }
        modified = card.timesUpgraded != 0 || card.upgraded;
        upgraded = card.timesUpgraded != 0 || card.upgraded;
        return upgraded;
    }
}
