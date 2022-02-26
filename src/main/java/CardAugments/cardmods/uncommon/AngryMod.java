package CardAugments.cardmods.uncommon;

import CardAugments.CardAugmentsMod;
import CardAugments.cardmods.AbstractAugment;
import basemod.abstracts.AbstractCardModifier;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDiscardAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;

public class AngryMod extends AbstractAugment {
    public static final String ID = CardAugmentsMod.makeID("AngryMod");
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;

    @Override
    public void onInitialApplication(AbstractCard card) {
        if (card.baseDamage > 0) {
            modifyBaseStat(card, BuffType.DAMAGE, BuffScale.MINOR_DEBUFF);
        }
        if (card.baseBlock > 0) {
            modifyBaseStat(card, BuffType.BLOCK, BuffScale.MINOR_DEBUFF);
        }
    }

    @Override
    public boolean validCard(AbstractCard card) {
        return isNormalCard(card) && card.cost != -2;
    }

    @Override
    public String modifyName(String cardName, AbstractCard card) {
        return TEXT[0] + cardName + TEXT[1];
    }

    @Override
    public String modifyDescription(String rawDescription, AbstractCard card) {
        if (rawDescription.contains(TEXT[4])) {
            return rawDescription.replace(TEXT[4], TEXT[5]);
        }
        return rawDescription + TEXT[2];
    }

    @Override
    public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
        this.addToBot(new MakeTempCardInDiscardAction(card.makeStatEquivalentCopy(), 1));
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.UNCOMMON;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new AngryMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}
