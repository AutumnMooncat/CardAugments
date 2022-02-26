package CardAugments.cardmods.rare;

import CardAugments.CardAugmentsMod;
import CardAugments.cardmods.AbstractAugment;
import basemod.AutoAdd;
import basemod.abstracts.AbstractCardModifier;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.actions.watcher.ChangeStanceAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.purple.*;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.stances.WrathStance;

import java.util.Arrays;
import java.util.List;

public class PhilosophersMod extends AbstractAugment {
    public static final String ID = CardAugmentsMod.makeID(PhilosophersMod.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;

    public static final int STRENGTH = 1;

    @Override
    public void onInitialApplication(AbstractCard card) {
        card.cost = card.cost - 1;
        card.costForTurn = card.cost;
    }

    @Override
    public boolean canRoll(AbstractCard card) {
        AbstractCard upgradeCheck = card.makeCopy();
        upgradeCheck.upgrade();
        return card.cost == upgradeCheck.cost && validCard(card);
    }

    @Override
    public boolean validCard(AbstractCard card) {
        return card.cost > 0;
    }

    @Override
    public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
        for (AbstractMonster m: AbstractDungeon.getMonsters().monsters)
            if (!m.isDeadOrEscaped())
                addToBot(new ApplyPowerAction(m, AbstractDungeon.player, new StrengthPower(m, STRENGTH), STRENGTH));
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
    public AugmentRarity getModRarity() {
        return AugmentRarity.UNCOMMON;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new PhilosophersMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}
