package CardAugments.cardmods.uncommon;

import CardAugments.CardAugmentsMod;
import CardAugments.cardmods.AbstractAugment;
import basemod.abstracts.AbstractCardModifier;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.actions.watcher.SanctityAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.purple.Sanctity;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class SanctifiedMod extends AbstractAugment {
    public static final String ID = CardAugmentsMod.makeID("SanctifiedMod");
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;

    private static final int CARDS = 2;

    @Override
    public void onInitialApplication(AbstractCard card) {
        if (card instanceof Sanctity) {
            card.baseMagicNumber += CARDS;
            card.magicNumber = card.baseMagicNumber;
        }
    }

    @Override
    public boolean validCard(AbstractCard card) {
        return card.cost != -2 && isNormalCard(card);
    }

    @Override
    public String modifyName(String cardName, AbstractCard card) {
        return TEXT[0] + cardName + TEXT[1];
    }

    @Override
    public String modifyDescription(String rawDescription, AbstractCard card) {
        if (card instanceof Sanctity) {
            return rawDescription;
        }
        return rawDescription + String.format(TEXT[2], CARDS);
    }

    @Override
    public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
        if (!(card instanceof Sanctity)) {
            this.addToBot(new SanctityAction(CARDS));
        }
    }

    @Override
    public AbstractAugment.AugmentRarity getModRarity() {
        return AbstractAugment.AugmentRarity.UNCOMMON;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new SanctifiedMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }

    public static boolean glowCheck(AbstractCard card) {
        return !(card instanceof Sanctity) && (!AbstractDungeon.actionManager.cardsPlayedThisCombat.isEmpty() && (AbstractDungeon.actionManager.cardsPlayedThisCombat.get(AbstractDungeon.actionManager.cardsPlayedThisCombat.size() - 1)).type == AbstractCard.CardType.SKILL);
    }
}
