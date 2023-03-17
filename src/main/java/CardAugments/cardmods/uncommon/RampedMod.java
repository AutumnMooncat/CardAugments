package CardAugments.cardmods.uncommon;

import CardAugments.CardAugmentsMod;
import CardAugments.cardmods.AbstractAugment;
import CardAugments.cardmods.DynvarCarrier;
import basemod.abstracts.AbstractCardModifier;
import com.megacrit.cardcrawl.actions.common.ModifyDamageAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class RampedMod extends AbstractAugment implements DynvarCarrier {
    public static final String ID = CardAugmentsMod.makeID(RampedMod.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;
    private static final String KEY = "!" + ID + "!";
    public int baseAmount;
    public int upAmount;
    public boolean modified;
    public boolean upgraded;

    public int getBaseVal(AbstractCard card) {
        return baseAmount + getEffectiveUpgrades(card) * upAmount;
    }

    @Override
    public float modifyBaseDamage(float damage, DamageInfo.DamageType type, AbstractCard card, AbstractMonster target) {
        return damage - baseAmount;
    }

    @Override
    public void onInitialApplication(AbstractCard card) {
        AbstractCard base = makeNewInstance(card);
        if (base != null) {
            baseAmount = (int) Math.ceil(base.baseDamage * (1 - MAJOR_DEBUFF));
            base.upgrade();
            upAmount = (int) Math.ceil(base.baseDamage * (1 - MAJOR_DEBUFF)) - baseAmount;
        }
    }

    @Override
    public boolean validCard(AbstractCard card) {
        return card.cost != -2 && card.baseDamage >= 3 && card.type == AbstractCard.CardType.ATTACK && makeNewInstance(card) != null && cardCheck(card, c -> doesntExhaust(c));
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
    public String modifyDescription(String rawDescription, AbstractCard card) {
        return rawDescription + String.format(TEXT[2], KEY);
    }

    @Override
    public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
        this.addToBot(new ModifyDamageAction(card.uuid, getBaseVal(card)));
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.UNCOMMON;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new RampedMod();
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
