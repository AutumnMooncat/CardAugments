package CardAugments.cutStuff;

import CardAugments.CardAugmentsMod;
import CardAugments.cardmods.AbstractAugment;
import CardAugments.patches.InterruptUseCardFieldPatches;
import CardAugments.patches.MultiPreviewFieldPatches;
import CardAugments.powers.InfinitePower;
import CardAugments.util.FormatHelper;
import CardAugments.util.PortraitHelper;
import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class InfiniteMod extends AbstractAugment {
    public static final String ID = CardAugmentsMod.makeID(InfiniteMod.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;

    private boolean inherentHack = true;

    @Override
    public void onInitialApplication(AbstractCard card) {
        AbstractCard preview = card.makeStatEquivalentCopy();
        inherentHack = false;
        CardModifierManager.addModifier(preview, new InfiniteGeneratedMod());
        MultiPreviewFieldPatches.addPreview(card, preview);
        if (card.cardsToPreview != null) {
            MultiPreviewFieldPatches.addPreview(card, card.cardsToPreview);
            card.cardsToPreview = null;
        }
        card.cost = card.costForTurn = 1;
        card.type = AbstractCard.CardType.POWER;
        card.target = AbstractCard.CardTarget.SELF;
        PortraitHelper.setMaskedPortrait(card);
        InterruptUseCardFieldPatches.InterceptUseField.interceptUse.set(card, true);
    }

    @Override
    public boolean validCard(AbstractCard card) {
        return doesntExhaust(card) && doesntOverride(card, "canUse", AbstractPlayer.class, AbstractMonster.class);
    }

    @Override
    public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
        inherentHack = true;
        AbstractCard copy = card.makeStatEquivalentCopy();
        inherentHack = false;
        CardModifierManager.addModifier(copy, new InfiniteGeneratedMod());
        addToBot(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new InfinitePower(AbstractDungeon.player, copy)));
    }

    @Override
    public void onUpgradeCheck(AbstractCard card) {
        card.cardsToPreview.upgrade();
        card.initializeDescription();
    }

    @Override
    public String getPrefix() {
        return TEXT[0];
    }

    @Override
    public String getSufix() {
        return TEXT[1];
    }

    @Override
    public String modifyDescription(String rawDescription, AbstractCard card) {
        return String.format(TEXT[2], FormatHelper.prefixWords(card.name, "*"));
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

    @Override
    public boolean isInherent(AbstractCard card) {
        return inherentHack;
    }
}
