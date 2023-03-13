package CardAugments.cardmods.curse;

import CardAugments.CardAugmentsMod;
import CardAugments.cardmods.AbstractAugment;
import basemod.abstracts.AbstractCardModifier;
import com.megacrit.cardcrawl.actions.unique.LoseEnergyAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;

public class VoidingMod extends AbstractAugment {
    public static final String ID = CardAugmentsMod.makeID(VoidingMod.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;

    @Override
    public void onInitialApplication(AbstractCard card) {
        card.isEthereal = true;
        card.exhaust = true;
    }

    @Override
    public boolean validCard(AbstractCard card) {
        return card.type == AbstractCard.CardType.CURSE && card.cost == -2 && cardCheck(card, c -> doesntUpgradeCost() && notEthereal(c) && notExhaust(c));
    }

    @Override
    public void onDrawn(AbstractCard card) {
        addToBot(new LoseEnergyAction(1));
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
        return TEXT[2] + rawDescription + TEXT[3];
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.RARE;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new VoidingMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}
