package CardAugments.cardmods.rare;

import CardAugments.CardAugmentsMod;
import CardAugments.cardmods.AbstractAugment;
import CardAugments.cardmods.util.BetaMod;
import CardAugments.cardmods.util.OmegaMod;
import CardAugments.patches.InterruptUseCardFieldPatches;
import CardAugments.patches.MultiPreviewFieldPatches;
import CardAugments.util.FormatHelper;
import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDrawPileAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class AlphaMod extends AbstractAugment {
    public static final String ID = CardAugmentsMod.makeID("AlphaMod");
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;

    private boolean inherentHack = true;

    @Override
    public void onInitialApplication(AbstractCard card) {
        AbstractCard preview = card.makeStatEquivalentCopy();
        inherentHack = false;
        CardModifierManager.addModifier(preview, new BetaMod());
        card.cardsToPreview = null;
        MultiPreviewFieldPatches.addPreview(card, preview);
        InterruptUseCardFieldPatches.InterceptUseField.interceptUse.set(card, true);
        card.isEthereal = false;
        if (card.type != AbstractCard.CardType.POWER) {
            card.exhaust = true;
        }
    }

    @Override
    public boolean validCard(AbstractCard card) {
        return (card.baseDamage > 0 || card.baseBlock > 0 || usesMagic(card)) && doesntOverride(card, "canUse", AbstractPlayer.class, AbstractMonster.class);
    }

    @Override
    public String modifyName(String cardName, AbstractCard card) {
        return TEXT[0] + cardName + TEXT[1];
    }

    @Override
    public String modifyDescription(String rawDescription, AbstractCard card) {
        return (card.isInnate ? TEXT[4] : "") + String.format(TEXT[2], FormatHelper.prefixWords(card.name, "*")) + (card.type == AbstractCard.CardType.POWER ? "" : TEXT[3]);
    }

    @Override
    public void onUpgradeCheck(AbstractCard card) {
        for (AbstractCard c : MultiPreviewFieldPatches.ExtraPreviews.previews.get(card)) {
            if (CardModifierManager.hasModifier(c, BetaMod.ID) || CardModifierManager.hasModifier(c, OmegaMod.ID)) {
                c.upgrade();
            }
        }
    }

    @Override
    public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
        inherentHack = true;
        AbstractCard copy = card.makeStatEquivalentCopy();
        inherentHack = false;
        CardModifierManager.addModifier(copy, new BetaMod());
        this.addToBot(new MakeTempCardInDrawPileAction(copy, 1, true, true));
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
