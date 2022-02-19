package CardAugments.cardmods.rare;

import CardAugments.CardAugmentsMod;
import CardAugments.actions.RandomCostToHandAction;
import CardAugments.cardmods.AbstractAugment;
import basemod.abstracts.AbstractCardModifier;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;

public class ForOneMod extends AbstractAugment {
    public static final String ID = CardAugmentsMod.makeID("ForOneMod");
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;

    @Override
    public boolean validCard(AbstractCard card) {
        return card.cost != -2 && card.cost != 0 && isNormalCard(card);
    }

    @Override
    public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
        this.addToBot(new RandomCostToHandAction(0));
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
        return AugmentRarity.RARE;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new ForOneMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}
