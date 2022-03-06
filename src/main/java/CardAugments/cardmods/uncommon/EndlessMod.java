package CardAugments.cardmods.uncommon;

import CardAugments.CardAugmentsMod;
import CardAugments.cardmods.AbstractAugment;
import basemod.abstracts.AbstractCardModifier;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;

public class EndlessMod extends AbstractAugment {
    public static final String ID = CardAugmentsMod.makeID("EndlessMod");
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;

    private boolean setExhaust;

    @Override
    public void onInitialApplication(AbstractCard card) {
        if (card.baseDamage > 0) {
            modifyBaseStat(card, BuffType.DAMAGE, BuffScale.MINOR_DEBUFF);
        }
        if (card.baseBlock > 0) {
            modifyBaseStat(card, BuffType.BLOCK, BuffScale.MINOR_DEBUFF);
        }
        if (card.type != AbstractCard.CardType.POWER && !card.exhaust) {
            card.exhaust = true;
            setExhaust = true;
        }
    }

    @Override
    public boolean validCard(AbstractCard card) {
        return isNormalCard(card) && doesntUpgradeExhaust(card);
    }

    @Override
    public String modifyName(String cardName, AbstractCard card) {
        return TEXT[0] + cardName + TEXT[1];
    }

    @Override
    public String modifyDescription(String rawDescription, AbstractCard card) {
        if (rawDescription.contains(TEXT[5])) {
            return rawDescription.replace(TEXT[5], TEXT[6]);
        }
        return rawDescription + TEXT[2] + (setExhaust ? TEXT[3] : "");
    }

    @Override
    public void onDrawn(AbstractCard card) {
        this.addToBot(new MakeTempCardInHandAction(card.makeStatEquivalentCopy()));
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.UNCOMMON;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new EndlessMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}
