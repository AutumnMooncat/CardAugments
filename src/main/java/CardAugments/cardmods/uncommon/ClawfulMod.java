package CardAugments.cardmods.uncommon;

import CardAugments.CardAugmentsMod;
import CardAugments.actions.ScaleAllByPredAction;
import CardAugments.cardmods.AbstractAugment;
import CardAugments.cardmods.DynvarCarrier;
import basemod.abstracts.AbstractCardModifier;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.blue.Claw;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class ClawfulMod extends AbstractAugment implements DynvarCarrier {
    public static final String ID = CardAugmentsMod.makeID(ClawfulMod.class.getSimpleName());
    public static final String DESCRIPTION_KEY = "!"+ID+"!";
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;

    private static final int EFFECT = 2;
    private static final int UPGRADE_EFFECT = 1;

    public int val;
    public boolean modified;
    public boolean upgraded;

    public boolean strikeMode;

    public int getBaseVal(AbstractCard card) {
        return EFFECT + getEffectiveUpgrades(card) * UPGRADE_EFFECT;
    }

    @Override
    public void onInitialApplication(AbstractCard card) {
        val = getBaseVal(card);
        strikeMode = card.hasTag(AbstractCard.CardTags.STRIKE);
    }

    @Override
    public float modifyBaseDamage(float damage, DamageInfo.DamageType type, AbstractCard card, AbstractMonster target) {
        return damage -2;
    }

    @Override
    public float modifyBaseMagic(float magic, AbstractCard card) {
        if (card instanceof Claw) {
            val = getBaseVal(card);
            return magic + val;
        }
        return magic;
    }

    @Override
    public void updateDynvar(AbstractCard card) {
        val = getBaseVal(card);
        modified = false;
    }

    @Override
    public boolean validCard(AbstractCard card) {
        return card.cost >= 0 && card.baseDamage >= 3 && card.type == AbstractCard.CardType.ATTACK;
    }

    @Override
    public String getPrefix() {
        return strikeMode ? TEXT[2] : TEXT[0];
    }

    @Override
    public String getSuffix() {
        return strikeMode ? TEXT[3] : TEXT[1];
    }

    @Override
    public String modifyDescription(String rawDescription, AbstractCard card) {
        if (card instanceof Claw) {
            return rawDescription;
        }
        if (strikeMode) {
            return rawDescription + String.format(TEXT[5], DESCRIPTION_KEY);
        }
        return rawDescription + String.format(TEXT[4], card.originalName, DESCRIPTION_KEY);
    }

    @Override
    public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
        if (card instanceof Claw) {
            return;
        }
        if (strikeMode) {
            addToBot(new ScaleAllByPredAction(card, val, ScaleAllByPredAction.StatBoost.DAMAGE, c -> c.hasTag(AbstractCard.CardTags.STRIKE)));
        } else {
            addToBot(new ScaleAllByPredAction(card, val, ScaleAllByPredAction.StatBoost.DAMAGE, c -> c.getClass().equals(card.getClass())));
        }
    }

    @Override
    public void onUpgradeCheck(AbstractCard card) {
        if (card instanceof Claw) {
            card.upgradedMagicNumber = true;
        }
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.UNCOMMON;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new ClawfulMod();
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
