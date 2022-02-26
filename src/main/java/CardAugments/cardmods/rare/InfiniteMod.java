package CardAugments.cardmods.rare;

import CardAugments.CardAugmentsMod;
import CardAugments.cardmods.AbstractAugment;
import CardAugments.cardmods.util.InfiniteGeneratedMod;
import CardAugments.patches.InterruptUseCardFieldPatches;
import CardAugments.powers.InfinitePower;
import CardAugments.util.PortraitHelper;
import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class InfiniteMod extends AbstractAugment {
    public static final String ID = CardAugmentsMod.makeID(InfiniteMod.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;

    @Override
    public void onInitialApplication(AbstractCard card) {
        card.cardsToPreview = card.makeCopy();
        CardModifierManager.addModifier(card.cardsToPreview, new InfiniteGeneratedMod());
        for (int i=0; i<card.timesUpgraded; i++)
            card.cardsToPreview.upgrade();
        card.cardsToPreview.misc = card.misc;

        card.cost = card.costForTurn = 1;
        card.type = AbstractCard.CardType.POWER;
        card.target = AbstractCard.CardTarget.SELF;
        PortraitHelper.setMaskedPortrait(card);
        InterruptUseCardFieldPatches.InterceptUseField.interceptUse.set(card, true);
    }

    @Override
    public boolean validCard(AbstractCard card) {
        return cardDoesntExhaust(card);
    }

    @Override
    public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
        addToBot(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new InfinitePower(AbstractDungeon.player, card.cardsToPreview)));
    }

    @Override
    public void onUpgradeCheck(AbstractCard card) {
        card.cardsToPreview.upgrade();
    }

    @Override
    public String modifyName(String cardName, AbstractCard card) {
        return TEXT[0] + cardName + TEXT[1];
    }

    @Override
    public String modifyDescription(String rawDescription, AbstractCard card) {
        return String.format(TEXT[2], card.name);
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.RARE;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new InfiniteMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}
