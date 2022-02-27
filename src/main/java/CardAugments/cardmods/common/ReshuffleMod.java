package CardAugments.cardmods.common;

import CardAugments.CardAugmentsMod;
import CardAugments.cardmods.AbstractAugment;
import basemod.abstracts.AbstractCardModifier;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;

public class ReshuffleMod extends AbstractAugment {
    public static final String ID = CardAugmentsMod.makeID("ReshuffleMod");
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;

    @Override
    public void onInitialApplication(AbstractCard card) {
        card.shuffleBackIntoDrawPile = true;
    }

    @Override
    public boolean validCard(AbstractCard card) {
        return card.cost != -2 && !card.shuffleBackIntoDrawPile && cardDoesntExhaust(card) && card.type != AbstractCard.CardType.POWER;
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
        return AugmentRarity.COMMON;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new ReshuffleMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}
