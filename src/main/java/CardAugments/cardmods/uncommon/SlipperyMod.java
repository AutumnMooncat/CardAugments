package CardAugments.cardmods.uncommon;

import CardAugments.CardAugmentsMod;
import CardAugments.cardmods.AbstractAugment;
import basemod.abstracts.AbstractCardModifier;
import com.megacrit.cardcrawl.actions.common.DiscardSpecificCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.colorless.PanicButton;
import com.megacrit.cardcrawl.core.CardCrawlGame;

public class SlipperyMod extends AbstractAugment {
    public static final String ID = CardAugmentsMod.makeID("SlipperyMod");
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;

    private boolean discarded;

    @Override
    public void onInitialApplication(AbstractCard card) {
        AbstractCard upgradeCheck = card.makeCopy();
        upgradeCheck.upgrade();
        if (card.baseDamage > 0) {
            modifyBaseStat(card, BuffType.DAMAGE, BuffScale.HUGE_BUFF);
        }
        if (card.baseBlock > 0) {
            modifyBaseStat(card, BuffType.BLOCK, BuffScale.HUGE_BUFF);
        }
        if (usesMagic(card) && card.baseMagicNumber <= upgradeCheck.baseMagicNumber && !(card instanceof PanicButton)) {
            modifyBaseStat(card, BuffType.MAGIC, BuffScale.HUGE_BUFF);
        }
    }

    @Override
    public boolean validCard(AbstractCard card) {
        return card.cost == -2 || card.baseDamage > 0 || card.baseBlock > 0 || usesMagic(card);
    }

    @Override
    public void onDrawn(AbstractCard card) {
        if (!discarded) {
            discarded = true;
            this.addToBot(new DiscardSpecificCardAction(card));
        }
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
    public AugmentRarity getModRarity() {
        return AugmentRarity.UNCOMMON;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new SlipperyMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}
