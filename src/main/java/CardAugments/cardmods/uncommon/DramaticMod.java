package CardAugments.cardmods.uncommon;

import CardAugments.CardAugmentsMod;
import CardAugments.cardmods.AbstractAugment;
import basemod.abstracts.AbstractCardModifier;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;

public class DramaticMod extends AbstractAugment {
    public static final String ID = CardAugmentsMod.makeID("DramaticMod");
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;

    @Override
    public void onInitialApplication(AbstractCard card) {
        if (card.baseDamage > 0) {
            modifyBaseStat(card, BuffType.DAMAGE, BuffScale.MAJOR_BUFF);
        }
        if (card.baseBlock > 0) {
            modifyBaseStat(card, BuffType.BLOCK, BuffScale.MAJOR_BUFF);
        }
        card.exhaust = true;
        card.isInnate = true;
    }

    @Override
    public boolean validCard(AbstractCard card) {
        return (card.baseDamage > 0 || card.baseBlock > 0) && card.type != AbstractCard.CardType.POWER && cardCheck(card, c -> notInnate(c) && notExhaust(c));
    }

    @Override
    public String modifyName(String cardName, AbstractCard card) {
        return TEXT[0] + cardName + TEXT[1];
    }

    @Override
    public String modifyDescription(String rawDescription, AbstractCard card) {
        return TEXT[2] + rawDescription + TEXT[3];
    }

    @Override
    public AbstractAugment.AugmentRarity getModRarity() {
        return AugmentRarity.UNCOMMON;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new DramaticMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}
