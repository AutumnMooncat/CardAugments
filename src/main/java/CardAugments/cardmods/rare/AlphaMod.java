package CardAugments.cardmods.rare;

import CardAugments.CardAugmentsMod;
import CardAugments.cardmods.AbstractAugment;
import CardAugments.cardmods.util.BetaMod;
import CardAugments.cardmods.util.OmegaMod;
import CardAugments.cardmods.util.PreviewedMod;
import CardAugments.patches.InterruptUseCardFieldPatches;
import CardAugments.util.FormatHelper;
import CardAugments.util.PortraitHelper;
import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import basemod.patches.com.megacrit.cardcrawl.cards.AbstractCard.MultiCardPreview;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDrawPileAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;

public class AlphaMod extends AbstractAugment {
    public static final String ID = CardAugmentsMod.makeID(AlphaMod.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;

    private boolean inherentHack = true;
    private AbstractCard.CardTarget originalTarget;

    @Override
    public void onInitialApplication(AbstractCard card) {
        AbstractCard preview1 = card.makeStatEquivalentCopy();
        AbstractCard preview2 = card.makeStatEquivalentCopy();
        inherentHack = false;
        CardModifierManager.addModifier(preview1, new BetaMod());
        CardModifierManager.addModifier(preview2, new OmegaMod());
        MultiCardPreview.add(card, preview1, preview2);
        InterruptUseCardFieldPatches.InterceptUseField.interceptUse.set(card, true);
        card.isEthereal = false;
        card.exhaust = true;
        originalTarget = card.target;
        card.target = AbstractCard.CardTarget.NONE;
        if (card.type != AbstractCard.CardType.SKILL) {
            card.type = AbstractCard.CardType.SKILL;
            PortraitHelper.setMaskedPortrait(card);
        }
    }

    @Override
    public boolean validCard(AbstractCard card) {
        return (card.baseDamage > 0 || card.baseBlock > 0 || usesMagic(card)) && noShenanigans(card);
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
    public String modifyDescription(String rawDescription, AbstractCard card) {
        return (card.isInnate ? TEXT[4] : "") + String.format(TEXT[2], FormatHelper.prefixWords(card.name, "*")) + (card.type == AbstractCard.CardType.POWER ? "" : TEXT[3]);
    }

    @Override
    public void onUpgradeCheck(AbstractCard card) {
        for (AbstractCard c : MultiCardPreview.multiCardPreview.get(card)) {
            if (CardModifierManager.hasModifier(c, BetaMod.ID) || CardModifierManager.hasModifier(c, OmegaMod.ID)) {
                c.upgrade();
                CardModifierManager.testBaseValues(c);
                c.initializeDescription();
            }
        }
        card.initializeDescription();
    }

    @Override
    public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
        AbstractCard preview = null;
        for (AbstractCard c : MultiCardPreview.multiCardPreview.get(card)) {
            if (CardModifierManager.hasModifier(c, BetaMod.ID)) {
                preview = c;
            }
        }
        if (preview != null) {
            AbstractCard copy = preview.makeStatEquivalentCopy();
            this.addToBot(new MakeTempCardInDrawPileAction(copy, 1, true, true));
        }
        /*inherentHack = true;
        AbstractCard copy = card.makeStatEquivalentCopy();
        copy.target = originalTarget;
        inherentHack = false;
        action.exhaustCard = true;
        CardModifierManager.addModifier(copy, new BetaMod());
        this.addToBot(new MakeTempCardInDrawPileAction(copy, 1, true, true));*/
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.RARE;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new AlphaMod();
    }

    @Override
    public boolean isInherent(AbstractCard card) {
        return inherentHack;
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }

}
