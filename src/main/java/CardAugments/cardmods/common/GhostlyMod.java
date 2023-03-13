package CardAugments.cardmods.common;

import CardAugments.CardAugmentsMod;
import CardAugments.cardmods.AbstractAugment;
import basemod.abstracts.AbstractCardModifier;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;

public class GhostlyMod extends AbstractAugment {
    public static final String ID = CardAugmentsMod.makeID(GhostlyMod.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;

    @Override
    public void onInitialApplication(AbstractCard card) {
        card.isEthereal = true;
    }

    @Override
    public float modifyBaseBlock(float block, AbstractCard card) {
        return block * MAJOR_BUFF;
    }

    @Override
    public boolean validCard(AbstractCard card) {
        return card.baseBlock > 0 && cardCheck(card, c -> notRetain(c) && notEthereal(c));
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
        return TEXT[2] + rawDescription;
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.COMMON;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new GhostlyMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}
