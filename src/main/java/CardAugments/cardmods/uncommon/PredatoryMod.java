package CardAugments.cardmods.uncommon;

import CardAugments.CardAugmentsMod;
import CardAugments.cardmods.AbstractAugment;
import basemod.abstracts.AbstractCardModifier;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.DrawCardNextTurnPower;

public class PredatoryMod extends AbstractAugment {
    public static final String ID = CardAugmentsMod.makeID(PredatoryMod.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;

    private static final int DRAW = 2;

    @Override
    public void onInitialApplication(AbstractCard card) {
        card.cost = card.cost + 1;
        card.costForTurn = card.cost;
    }

    @Override
    public float modifyBaseDamage(float damage, DamageInfo.DamageType type, AbstractCard card, AbstractMonster target) {
        return damage * MAJOR_BUFF;
    }

    @Override
    public boolean validCard(AbstractCard card) {
        return card.cost >= 0 && card.type == AbstractCard.CardType.ATTACK && cardCheck(card, c -> c.baseDamage >= 3 && doesntUpgradeCost());
    }

    @Override
    public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
        this.addToBot(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new DrawCardNextTurnPower(AbstractDungeon.player, DRAW)));
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
        if (rawDescription.contains(TEXT[4])) {
            return rawDescription.replace(TEXT[4], TEXT[5]);
        }
        return rawDescription + String.format(TEXT[2], DRAW);
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.UNCOMMON;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new PredatoryMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }

}
