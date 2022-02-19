package CardAugments.cardmods.uncommon;

import CardAugments.CardAugmentsMod;
import CardAugments.cardmods.AbstractAugment;
import basemod.abstracts.AbstractCardModifier;
import com.megacrit.cardcrawl.actions.defect.ChannelAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.orbs.Dark;

public class GloomMod extends AbstractAugment {
    public static final String ID = CardAugmentsMod.makeID("GloomMod");
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;

    private static final int ORBS = 1;

    @Override
    public void onInitialApplication(AbstractCard card) {
        if (card.baseDamage > 0) {
           modifyBaseStat(card, BuffType.DAMAGE, BuffScale.MODERATE_DEBUFF);
        }
        if (card.baseBlock > 0) {
            modifyBaseStat(card, BuffType.BLOCK, BuffScale.MODERATE_DEBUFF);
        }
    }

    @Override
    public boolean validCard(AbstractCard card) {
        return card.cost != -2 && allowOrbMods() && isNormalCard(card) && (card.baseDamage > 0 || card.baseBlock > 0);
    }

    @Override
    public String modifyName(String cardName, AbstractCard card) {
        return TEXT[0] + cardName + TEXT[1];
    }

    @Override
    public String modifyDescription(String rawDescription, AbstractCard card) {
        return rawDescription + String.format(TEXT[2], ORBS);
    }

    @Override
    public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
        this.addToBot(new ChannelAction(new Dark()));
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.UNCOMMON;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new GloomMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}
