package CardAugments.cardmods.common;

import CardAugments.CardAugmentsMod;
import CardAugments.cardmods.AbstractAugment;
import basemod.abstracts.AbstractCardModifier;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;

public class BrutalMod extends AbstractAugment {
    public static final String ID = CardAugmentsMod.makeID("BrutalMod");
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;

    @Override
    public void onInitialApplication(AbstractCard card) {
        modifyBaseStat(card, BuffType.DAMAGE, BuffScale.MAJOR_BUFF);
        card.isEthereal = true;
    }

    @Override
    public boolean validCard(AbstractCard card) {
        return card.baseDamage > 0 && !card.isEthereal && card.type != AbstractCard.CardType.POWER;
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
        return new BrutalMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}
