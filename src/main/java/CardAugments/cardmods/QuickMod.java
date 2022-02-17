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
        card.baseDamage -= getDamageNerf(card);
        card.damage = card.baseDamage;
    }

    public int getDamageNerf(AbstractCard card) {
        AbstractCard upgrade = card.makeCopy();
        card.upgrade();
        int check = Math.max(card.baseDamage, upgrade.baseDamage);
        if (check <= 5) {
            return 1;
        } else if (check <= 10) {
            return 2;
        } else if (check <= 15) {
            return 3;
        } else {
            return 4;
        }
    }

    @Override
    public boolean validCard(AbstractCard card) {
        return card.baseDamage > 1;
    }

    @Override
    public String modifyName(String cardName, AbstractCard card) {
        return TEXT[0] + cardName + TEXT[1];
    }

    @Override
    public String modifyDescription(String rawDescription, AbstractCard card) {
        return rawDescription + String.format(TEXT[2], CARDS);
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
        return ID;
    }
}
