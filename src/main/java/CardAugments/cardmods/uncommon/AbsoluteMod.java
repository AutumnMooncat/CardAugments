package CardAugments.cardmods.uncommon;

import CardAugments.CardAugmentsMod;
import CardAugments.cardmods.AbstractAugment;
import basemod.abstracts.AbstractCardModifier;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class AbsoluteMod extends AbstractAugment {
    public static final String ID = CardAugmentsMod.makeID("AbsoluteMod");
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;

    private static final int LOSS = 2;

    @Override
    public void onInitialApplication(AbstractCard card) {
        if (card.baseDamage > 0) {
            modifyBaseStat(card, BuffType.DAMAGE, BuffScale.HUGE_BUFF);
        }
        if (card.baseBlock > 0) {
            modifyBaseStat(card, BuffType.BLOCK, BuffScale.HUGE_BUFF);
        }
    }

    @Override
    public boolean validCard(AbstractCard card) {
        return (card.cost > 0 || card.cost == -1) && (card.baseDamage > 0 || card.baseBlock > 0);
    }

    @Override
    public String modifyName(String cardName, AbstractCard card) {
        return TEXT[0] + cardName + TEXT[1];
    }

    @Override
    public String modifyDescription(String rawDescription, AbstractCard card) {
        return rawDescription + TEXT[2];
    }

    @Override
    public boolean betterCanPlay(AbstractCard cardWithThisMod, AbstractCard cardToCheck) {
        if (cardWithThisMod == cardToCheck || hasThisMod(cardToCheck)) {
            return true;
        }
        cardToCheck.cantUseMessage = TEXT[4] + cardWithThisMod.name + TEXT[5];
        return false;
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.UNCOMMON;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new AbsoluteMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}
