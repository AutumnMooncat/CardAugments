package CardAugments.cardmods.event;

import CardAugments.CardAugmentsMod;
import CardAugments.cardmods.AbstractAugment;
import CardAugments.patches.EchoFieldPatches;
import basemod.abstracts.AbstractCardModifier;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;

public class EchoMod extends AbstractAugment {
    public static final String ID = CardAugmentsMod.makeID(EchoMod.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;
    private static final int AMOUNT = 1;

    @Override
    public void onInitialApplication(AbstractCard card) {
        EchoFieldPatches.EchoFields.echo.set(card, EchoFieldPatches.EchoFields.echo.get(card) + AMOUNT);
        card.exhaust = true;
    }

    @Override
    public boolean validCard(AbstractCard card) {
        return card.cost != -2 && (card.type == AbstractCard.CardType.ATTACK || card.type == AbstractCard.CardType.SKILL) && cardCheck(card, AbstractAugment::notExhaust);
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
        return rawDescription + TEXT[2];
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.SPECIAL;
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
