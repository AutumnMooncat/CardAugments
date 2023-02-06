package CardAugments.cardmods.uncommon;

import CardAugments.CardAugmentsMod;
import CardAugments.actions.AndTearAction;
import CardAugments.cardmods.AbstractAugment;
import CardAugments.cardmods.DynvarCarrier;
import CardAugments.util.CalcHelper;
import basemod.abstracts.AbstractCardModifier;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class TearMod extends AbstractAugment implements DynvarCarrier {
    public static final String ID = CardAugmentsMod.makeID(TearMod.class.getSimpleName());
    public static final String DESCRIPTION_KEY = "!"+ID+"!";
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;

    private static final int DAMAGE = 7;
    private static final int UPGRADE_DAMAGE = 2;

    public int val;
    public boolean modified;
    public boolean upgraded;

    public int getBaseVal(AbstractCard card) {
        return DAMAGE + getEffectiveUpgrades(card) * UPGRADE_DAMAGE;
    }

    @Override
    public void onInitialApplication(AbstractCard card) {
        val = getBaseVal(card);
    }

    @Override
    public float modifyBaseDamage(float damage, DamageInfo.DamageType type, AbstractCard card, AbstractMonster target) {
        return damage * MAJOR_DEBUFF;
    }

    @Override
    public void updateDynvar(AbstractCard card) {
        val = getBaseVal(card);
        modified = false;
    }

    @Override
    public void onApplyPowers(AbstractCard card) {
        val = CalcHelper.applyPowers(getBaseVal(card));
        modified = val != getBaseVal(card);
    }

    @Override
    public boolean validCard(AbstractCard card) {
        return card.cost != -2 && card.baseDamage > 1 && card.type == AbstractCard.CardType.ATTACK;
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
        return rawDescription + String.format(TEXT[2], DESCRIPTION_KEY);
    }

    @Override
    public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
        addToBot(new AndTearAction(getBaseVal(card)));
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

    @Override
    public String key() {
        return ID;
    }

    @Override
    public int val(AbstractCard card) {
        return val;
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
        val = getBaseVal(card);
        modified = card.timesUpgraded != 0 || card.upgraded;
        upgraded = card.timesUpgraded != 0 || card.upgraded;
        return upgraded;
    }
}
