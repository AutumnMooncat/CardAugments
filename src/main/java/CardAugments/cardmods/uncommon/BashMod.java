package CardAugments.cardmods.uncommon;

import CardAugments.CardAugmentsMod;
import CardAugments.cardmods.AbstractAugment;
import basemod.abstracts.AbstractCardModifier;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.blue.BeamCell;
import com.megacrit.cardcrawl.cards.purple.CrushJoints;
import com.megacrit.cardcrawl.cards.red.Bash;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.VulnerablePower;

public class BashMod extends AbstractAugment {
    public static final String ID = CardAugmentsMod.makeID(BashMod.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;

    public static final int EFFECT = 1;
    private boolean modifiedBase;

    @Override
    public void onInitialApplication(AbstractCard card) {
        if (card instanceof Bash || card instanceof BeamCell || card instanceof CrushJoints) {
            card.baseMagicNumber += EFFECT;
            card.magicNumber += EFFECT;
            modifiedBase = true;
        }
    }

    @Override
    public float modifyBaseDamage(float damage, DamageInfo.DamageType type, AbstractCard card, AbstractMonster target) {
        return damage * MODERATE_DEBUFF;
    }

    @Override
    public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
        if (!modifiedBase && target != null) {
            addToBot(new ApplyPowerAction(target, AbstractDungeon.player, new VulnerablePower(target, EFFECT, false)));
        }
    }

    @Override
    public boolean validCard(AbstractCard card) {
        return card.cost >= 0 && card.type == AbstractCard.CardType.ATTACK && card.baseDamage > 1 && cardCheck(card, c -> usesEnemyTargeting(c));
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
        if (modifiedBase) {
            return rawDescription;
        }
        return rawDescription + String.format(TEXT[2], EFFECT);
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.UNCOMMON;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new BashMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}
