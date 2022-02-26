package CardAugments.cardmods.util;

import CardAugments.CardAugmentsMod;
import CardAugments.cardmods.AbstractAugment;
import CardAugments.patches.InterruptUseCardFieldPatches;
import basemod.abstracts.AbstractCardModifier;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.colorless.PanicButton;
import com.megacrit.cardcrawl.core.CardCrawlGame;

public class OmegaMod extends AbstractAugment {
    public static final String ID = CardAugmentsMod.makeID("OmegaMod");
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;

    @Override
    public void onInitialApplication(AbstractCard card) {
        AbstractCard upgradeCheck = card.makeCopy();
        upgradeCheck.upgrade();
        if (card.baseDamage > 0) {
            modifyBaseStat(card, BuffType.DAMAGE, 2F);
        }
        if (card.baseBlock > 0) {
            modifyBaseStat(card, BuffType.BLOCK, 2F);
        }
        if (usesMagic(card) && card.baseMagicNumber <= upgradeCheck.baseMagicNumber && !(card instanceof PanicButton)) {
            modifyBaseStat(card, BuffType.MAGIC, 2F);
        }
        InterruptUseCardFieldPatches.InterceptUseField.interceptUse.set(card, false);
    }

    @Override
    public boolean validCard(AbstractCard card) {
        return true;
    }

    @Override
    public String modifyName(String cardName, AbstractCard card) {
        return TEXT[0] + cardName + TEXT[1];
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.SPECIAL;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new OmegaMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }

}
