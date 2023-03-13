package CardAugments.cardmods.curse;

import CardAugments.CardAugmentsMod;
import CardAugments.cardmods.AbstractAugment;
import CardAugments.util.FormatHelper;
import basemod.abstracts.AbstractCardModifier;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.GameDictionary;
import com.megacrit.cardcrawl.localization.LocalizedStrings;

public class ReturningMod extends AbstractAugment {
    public static final String ID = CardAugmentsMod.makeID(ReturningMod.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;

    @Override
    public void onInitialApplication(AbstractCard card) {
        card.cost = 0;
        card.costForTurn = card.cost;
        card.shuffleBackIntoDrawPile = true;
    }

    @Override
    public boolean validCard(AbstractCard card) {
        return card.type == AbstractCard.CardType.CURSE && card.cost == -2 && cardCheck(card, c -> doesntUpgradeCost() && notEthereal(c));
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
        for (String s : GameDictionary.UNPLAYABLE.NAMES) {
            rawDescription = rawDescription.replace(FormatHelper.capitalize(s) + LocalizedStrings.PERIOD + " NL ", "");
            rawDescription = rawDescription.replace(FormatHelper.capitalize(s) + LocalizedStrings.PERIOD, ""); // In case someone doesn't use a new line
        }
        return rawDescription + TEXT[2];
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.COMMON;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new ReturningMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}
