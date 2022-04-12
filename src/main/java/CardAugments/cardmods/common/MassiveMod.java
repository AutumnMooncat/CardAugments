package CardAugments.cardmods.common;

import CardAugments.CardAugmentsMod;
import CardAugments.cardmods.AbstractAugment;
import basemod.abstracts.AbstractCardModifier;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;

public class MassiveMod extends AbstractAugment {
    public static final String ID = CardAugmentsMod.makeID("MassiveMod");
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;

    @Override
    public void onInitialApplication(AbstractCard card) {
        if (card.baseDamage > 0) {
            modifyBaseStat(card, BuffType.DAMAGE, BuffScale.MAJOR_BUFF);
        }
        if (card.baseBlock > 0) {
            modifyBaseStat(card, BuffType.BLOCK, BuffScale.MAJOR_BUFF);
        }
        card.cost = card.cost + 1;
        card.costForTurn = card.cost;
    }

    @Override
    public boolean validCard(AbstractCard card) {
        return card.cost >= 0 && (card.baseDamage > 0 || card.baseBlock > 0) && cardCheck(card, c -> doesntUpgradeCost());
    }

    @Override
    public String modifyName(String cardName, AbstractCard card) {
        return TEXT[0] + cardName + TEXT[1];
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.COMMON;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new MassiveMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}
