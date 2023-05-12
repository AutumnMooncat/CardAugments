package CardAugments.cardmods.rare;

import CardAugments.CardAugmentsMod;
import CardAugments.cardmods.AbstractAugment;
import basemod.abstracts.AbstractCardModifier;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.StrengthPower;

public class PhilosophersMod extends AbstractAugment {
    public static final String ID = CardAugmentsMod.makeID(PhilosophersMod.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;
    public static final String[] CARD_TEXT = CardCrawlGame.languagePack.getUIString(ID).EXTRA_TEXT;

    public static final int STRENGTH = 1;

    @Override
    public void onInitialApplication(AbstractCard card) {
        card.cost = card.cost - 1;
        if (card.cost < 0) {
            card.cost = 0;
        }
        card.costForTurn = card.cost;
    }

    @Override
    public boolean validCard(AbstractCard card) {
        return card.cost > 0 && cardCheck(card, c -> doesntUpgradeCost());
    }

    @Override
    public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
        for (AbstractMonster m: AbstractDungeon.getMonsters().monsters)
            if (!m.isDeadOrEscaped())
                addToBot(new ApplyPowerAction(m, AbstractDungeon.player, new StrengthPower(m, STRENGTH), STRENGTH));
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
        return insertAfterText(rawDescription , CARD_TEXT[0]);
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.RARE;
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
