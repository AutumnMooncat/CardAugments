package CardAugments.cardmods.util;

import CardAugments.CardAugmentsMod;
import CardAugments.cardmods.AbstractAugment;
import CardAugments.powers.IcizePower;
import CardAugments.util.PortraitHelper;
import basemod.abstracts.AbstractCardModifier;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class InfiniteGeneratedMod extends AbstractAugment {
    public static final String ID = CardAugmentsMod.makeID(InfiniteGeneratedMod.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;

    @Override
    public void onInitialApplication(AbstractCard card) {
        card.exhaust = true;
        card.isEthereal = true;
        card.selfRetain = false;
    }

    @Override
    public boolean validCard(AbstractCard card) {
        return true;
    }

    @Override
    public String modifyDescription(String rawDescription, AbstractCard card) {
        String retVal = rawDescription;
        retVal = retVal.replace("Retain. NL ","");
        if (!rawDescription.contains(TEXT[0]))
            retVal = TEXT[0] + retVal;
        if (!rawDescription.contains(TEXT[1]) && card.type != AbstractCard.CardType.POWER)
            retVal = retVal + TEXT[1];
        return retVal;
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.SPECIAL;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new InfiniteGeneratedMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}
