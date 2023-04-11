package CardAugments.cardmods.rare;

import CardAugments.CardAugmentsMod;
import CardAugments.cardmods.AbstractAugment;
import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardBorderGlowManager;
import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.actions.watcher.SanctityAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.purple.Sanctity;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;

public class SanctifiedMod extends AbstractAugment {
    public static final String ID = CardAugmentsMod.makeID(SanctifiedMod.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;

    private static final int CARDS = 2;

    @Override
    public void onInitialApplication(AbstractCard card) {
        if (card instanceof Sanctity) {
            card.baseMagicNumber += CARDS;
            card.magicNumber = card.baseMagicNumber;
        }
    }

    @Override
    public boolean validCard(AbstractCard card) {
        return card.cost > 0;
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
        if (card instanceof Sanctity) {
            return rawDescription;
        }
        return rawDescription + String.format(TEXT[2], CARDS);
    }

    @Override
    public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
        if (!(card instanceof Sanctity)) {
            this.addToBot(new SanctityAction(CARDS));
        }
    }

    @Override
    public AbstractAugment.AugmentRarity getModRarity() {
        return AbstractAugment.AugmentRarity.RARE;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new SanctifiedMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }

    @Override
    public CardBorderGlowManager.GlowInfo getGlowInfo() {
        return new CardBorderGlowManager.GlowInfo() {
            @Override
            public boolean test(AbstractCard abstractCard) {
                return !(abstractCard instanceof Sanctity) && hasThisMod(abstractCard) && lastCardPlayedCheck(c -> c.type == AbstractCard.CardType.SKILL);
            }

            @Override
            public Color getColor(AbstractCard abstractCard) {
                return Color.GOLD.cpy();
            }

            @Override
            public String glowID() {
                return ID+"Glow";
            }
        };
    }
}
