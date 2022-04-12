package CardAugments.cardmods.rare;

import CardAugments.CardAugmentsMod;
import CardAugments.cardmods.AbstractAugment;
import basemod.abstracts.AbstractCardModifier;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.colorless.PanicButton;
import com.megacrit.cardcrawl.cards.purple.Halt;
import com.megacrit.cardcrawl.core.CardCrawlGame;

public class MK2Mod extends AbstractAugment {
    public static final String ID = CardAugmentsMod.makeID("MK2Mod");
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;

    @Override
    public void onInitialApplication(AbstractCard card) {
        if (card.baseDamage > 0) {
            modifyBaseStat(card, BuffType.DAMAGE, BuffScale.MINOR_BUFF);
        }
        if (card.baseBlock > 0) {
            modifyBaseStat(card, BuffType.BLOCK, BuffScale.MINOR_BUFF);
        }
        if (cardCheck(card, c -> doesntDowngradeMagic())) {
            modifyBaseStat(card, BuffType.MAGIC, BuffScale.MINOR_BUFF);
        }
    }

    @Override
    public boolean validCard(AbstractCard card) {
        return card.baseDamage > 0 || card.baseBlock > 0 || cardCheck(card, c -> doesntDowngradeMagic());
    }

    @Override
    public String modifyName(String cardName, AbstractCard card) {
        String[] nameParts = removeUpgradeText(cardName);
        return TEXT[0] + nameParts[0] + TEXT[1] + nameParts[1];
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.RARE;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new MK2Mod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}
