package CardAugments.cardmods.uncommon;

import CardAugments.CardAugmentsMod;
import CardAugments.cardmods.AbstractAugment;
import basemod.abstracts.AbstractCardModifier;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.blue.BeamCell;
import com.megacrit.cardcrawl.cards.blue.GoForTheEyes;
import com.megacrit.cardcrawl.cards.green.Neutralize;
import com.megacrit.cardcrawl.cards.green.SuckerPunch;
import com.megacrit.cardcrawl.cards.purple.CrushJoints;
import com.megacrit.cardcrawl.cards.purple.SashWhip;
import com.megacrit.cardcrawl.cards.red.Bash;
import com.megacrit.cardcrawl.cards.red.Clothesline;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.powers.WeakPower;

public class SuckerMod extends AbstractAugment {
    public static final String ID = CardAugmentsMod.makeID("SuckerMod");
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;

    public static final int EFFECT = 1;
    private boolean modifiedBase;

    @Override
    public void onInitialApplication(AbstractCard card) {
        if (card instanceof Clothesline || card instanceof Neutralize || card instanceof SuckerPunch || card instanceof GoForTheEyes || card instanceof SashWhip) {
            card.baseMagicNumber += EFFECT;
            card.magicNumber += EFFECT;
            modifiedBase = true;
        }
    }

    @Override
    public float modifyBaseDamage(float damage, DamageInfo.DamageType type, AbstractCard card, AbstractMonster target) {
        return damage * MINOR_DEBUFF;
    }

    @Override
    public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
        if (!modifiedBase && target != null) {
            addToBot(new ApplyPowerAction(target, AbstractDungeon.player, new WeakPower(target, EFFECT, false)));
        }
    }

    @Override
    public boolean validCard(AbstractCard card) {
        return card.cost >= 0 && card.type == AbstractCard.CardType.ATTACK && card.baseDamage > 1 && cardCheck(card, c -> targetsEnemy(c));
    }

    @Override
    public String modifyName(String cardName, AbstractCard card) {
        return TEXT[0] + cardName + TEXT[1];
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
        return new SuckerMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}
