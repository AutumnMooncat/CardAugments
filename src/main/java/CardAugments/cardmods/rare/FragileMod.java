package CardAugments.cardmods.rare;

import CardAugments.CardAugmentsMod;
import CardAugments.cardmods.AbstractAugment;
import basemod.abstracts.AbstractCardModifier;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;

public class FragileMod extends AbstractAugment {
    public static final String ID = CardAugmentsMod.makeID(FragileMod.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;

    @Override
    public void onInitialApplication(AbstractCard card) {
        card.cost = card.cost - 1;
        card.costForTurn = card.cost;
        card.exhaust = true;
    }

    @Override
    public boolean validCard(AbstractCard card) {
        return card.cost > 0 && card.type != AbstractCard.CardType.POWER && card.type != AbstractCard.CardType.CURSE && cardCheck(card, c -> doesntUpgradeCost() && doesntExhaust(c));
    }

    @Override
    public String getPrefix() {
        return TEXT[0];
    }

    @Override
    public String getSufix() {
        return TEXT[1];
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
        return new FragileMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}
