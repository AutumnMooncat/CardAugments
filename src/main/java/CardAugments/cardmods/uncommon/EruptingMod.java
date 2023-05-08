package CardAugments.cardmods.uncommon;

import CardAugments.CardAugmentsMod;
import CardAugments.cardmods.AbstractAugment;
import basemod.abstracts.AbstractCardModifier;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.actions.watcher.ChangeStanceAction;
import com.megacrit.cardcrawl.actions.watcher.IndignationAction;
import com.megacrit.cardcrawl.actions.watcher.InnerPeaceAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.powers.watcher.MantraPower;
import com.megacrit.cardcrawl.stances.WrathStance;

public class EruptingMod extends AbstractAugment {
    public static final String ID = CardAugmentsMod.makeID(EruptingMod.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;
    public static final String[] CARD_TEXT = CardCrawlGame.languagePack.getUIString(ID).EXTRA_TEXT;

    @Override
    public boolean validCard(AbstractCard card) {
        return card.color == AbstractCard.CardColor.PURPLE &&
                (card.type == AbstractCard.CardType.ATTACK || card.type == AbstractCard.CardType.SKILL) &&
                card.cost > -2 &&
                !usesAction(card, ChangeStanceAction.class) && !usesClass(card, MantraPower.class) &&
                !usesClass(card, IndignationAction.class) && !usesAction(card, InnerPeaceAction.class);
    }

    @Override
    public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
        addToBot(new ChangeStanceAction(WrathStance.STANCE_ID));
    }

    @Override
    public String getPrefix() {
        return TEXT[0];
    }

    @Override
    public String getSuffix() {
        return TEXT[1];
    }

    @Override
    public String getAugmentDescription() {
        return TEXT[2];
    }

    @Override
    public String modifyDescription(String rawDescription, AbstractCard card) {
        return insertAfterText(rawDescription , CARD_TEXT[0]);
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.UNCOMMON;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new EruptingMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}
