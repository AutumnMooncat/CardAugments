package CardAugments.cardmods;

import CardAugments.CardAugmentsMod;
import CardAugments.patches.EchoFieldPatches;
import CardAugments.util.FormatHelper;
import basemod.abstracts.AbstractCardModifier;
import com.evacipated.cardcrawl.mod.stslib.fields.cards.AbstractCard.ExhaustiveField;
import com.evacipated.cardcrawl.mod.stslib.patches.CommonKeywordIconsPatches;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.LocalizedStrings;

public class EchoMod extends AbstractAugment {
    public static final String ID = CardAugmentsMod.makeID("EchoMod");
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;
    public static String purgeName = CommonKeywordIconsPatches.purgeName;

    private static final int AMOUNT = 1;

    @Override
    public void onInitialApplication(AbstractCard card) {
        super.onInitialApplication(card);
        EchoFieldPatches.EchoFields.echo.set(card, EchoFieldPatches.EchoFields.echo.get(card) + AMOUNT);
        card.purgeOnUse = true;
    }

    @Override
    public boolean validCard(AbstractCard card) {
        return card.cost != -2 && isNormalCard(card) && !card.purgeOnUse && !card.exhaust && card.type != AbstractCard.CardType.POWER && (ExhaustiveField.ExhaustiveFields.baseExhaustive.get(card) == -1 || ExhaustiveField.ExhaustiveFields.exhaustive.get(card) == -1);
    }

    @Override
    public String modifyName(String cardName, AbstractCard card) {
        return TEXT[0] + cardName + TEXT[1];
    }

    @Override
    public String modifyDescription(String rawDescription, AbstractCard card) {
        return rawDescription + TEXT[2];// + " NL " + FormatHelper.capitalize(purgeName) + LocalizedStrings.PERIOD;
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.RARE;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new EchoMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}
