package CardAugments.cardmods.common;

import CardAugments.CardAugmentsMod;
import CardAugments.cardmods.AbstractAugment;
import basemod.abstracts.AbstractCardModifier;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;

public class GhostlyMod extends AbstractAugment {
    public static final String ID = CardAugmentsMod.makeID("GhostlyMod");
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;

    @Override
    public void onInitialApplication(AbstractCard card) {
        modifyBaseStat(card, BuffType.BLOCK, BuffScale.MAJOR_BUFF);
        card.isEthereal = true;
    }

    @Override
    public boolean validCard(AbstractCard card) {
        return card.baseBlock > 0 && !card.isEthereal && !card.selfRetain;
    }

    @Override
    public String modifyName(String cardName, AbstractCard card) {
        return TEXT[0] + cardName + TEXT[1];
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
