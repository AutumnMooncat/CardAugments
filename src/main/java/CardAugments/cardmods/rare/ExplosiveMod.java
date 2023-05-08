package CardAugments.cardmods.rare;

import CardAugments.CardAugmentsMod;
import CardAugments.cardmods.AbstractAugment;
import CardAugments.cardmods.util.PreviewedMod;
import CardAugments.patches.InterruptUseCardFieldPatches;
import CardAugments.powers.BombPower;
import CardAugments.util.FormatHelper;
import CardAugments.util.PortraitHelper;
import CardAugments.util.Wiz;
import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import basemod.patches.com.megacrit.cardcrawl.cards.AbstractCard.MultiCardPreview;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class ExplosiveMod extends AbstractAugment {
    public static final String ID = CardAugmentsMod.makeID(ExplosiveMod.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;
    public static final String[] CARD_TEXT = CardCrawlGame.languagePack.getUIString(ID).EXTRA_TEXT;
    public static final int COPIES = 5;
    private boolean inherentHack = true;

    @Override
    public void onInitialApplication(AbstractCard card) {
        inherentHack = true;
        AbstractCard preview = card.makeStatEquivalentCopy();
        inherentHack = false;
        CardModifierManager.addModifier(preview, new PreviewedMod());
        MultiCardPreview.add(card, preview);
        card.cost = 2;
        card.costForTurn = card.cost;
        card.isEthereal = false;
        card.exhaust = false;
        card.target = AbstractCard.CardTarget.SELF;
        InterruptUseCardFieldPatches.InterceptUseField.interceptUse.set(card, true);
        if (card.type != AbstractCard.CardType.SKILL) {
            card.type = AbstractCard.CardType.SKILL;
            PortraitHelper.setMaskedPortrait(card);
        }
    }

    @Override
    public boolean validCard(AbstractCard card) {
        return noShenanigans(card) && (card.cost == 0 || card.cost == 1) && card.type == AbstractCard.CardType.ATTACK && cardCheck(card, c -> doesntUpgradeCost() /*&& customCheck(check -> check.target == AbstractCard.CardTarget.ALL || check.target == AbstractCard.CardTarget.ALL_ENEMY)*/);
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
        return (card.isInnate ? CARD_TEXT[1] : "") + String.format(CARD_TEXT[0], COPIES, FormatHelper.prefixWords(card.name, "*"));
    }

    @Override
    public void onUpgradeCheck(AbstractCard card) {
        for (AbstractCard c : MultiCardPreview.multiCardPreview.get(card)) {
            if (CardModifierManager.hasModifier(c, PreviewedMod.ID)) {
                c.upgrade();
                c.initializeDescription();
            }
        }
        card.initializeDescription();
    }

    @Override
    public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
        AbstractCard preview = null;
        for (AbstractCard c : MultiCardPreview.multiCardPreview.get(card)) {
            if (CardModifierManager.hasModifier(c, PreviewedMod.ID)) {
                preview = c;
            }
        }
        if (preview != null) {
            AbstractCard copy = preview.makeStatEquivalentCopy();
            Wiz.applyToSelf(new BombPower(AbstractDungeon.player, 2, COPIES, copy));
        }
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.RARE;
    }

    @Override
    public boolean isInherent(AbstractCard card) {
        return inherentHack;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new ExplosiveMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}
