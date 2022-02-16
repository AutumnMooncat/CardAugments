package CardAugments.cardmods;

import CardAugments.CardAugmentsMod;
import basemod.abstracts.AbstractCardModifier;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;

public class QuickMod extends AbstractAugment {
    public static final String ID = CardAugmentsMod.makeID("QuickMod");
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;

    private static final int CARDS = 1;
    private static final float DMG_MULTI = 0.8f;

    @Override
    public void onInitialApplication(AbstractCard card) {
        super.onInitialApplication(card);
        card.baseDamage *= DMG_MULTI;
        card.damage = card.baseDamage;
    }

    @Override
    public boolean validCard(AbstractCard card) {
        return card.baseDamage > 0;
    }

    @Override
    public String getPrefix() {
        return TEXT[1];
    }

    @Override
    public String getSuffix() {
        return TEXT[2];
    }

    @Override
    public String modifyDescription(String rawDescription, AbstractCard card) {
        return rawDescription + String.format(TEXT[0], CARDS);
    }

    @Override
    public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
        addToBot(new DrawCardAction(CARDS));
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.COMMON;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new QuickMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return super.identifier(card);
    }
}
