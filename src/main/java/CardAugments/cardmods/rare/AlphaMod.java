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
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;

public class AlphaMod extends AbstractAugment {
    public static final String ID = CardAugmentsMod.makeID("AlphaMod");
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;

    private boolean inherentHack = true;

    @Override
    public void onInitialApplication(AbstractCard card) {
        AbstractCard preview = card.makeStatEquivalentCopy();
        //AbstractCard preview2 = card.makeStatEquivalentCopy();
        inherentHack = false;
        CardModifierManager.addModifier(preview, new BetaMod());
        //CardModifierManager.addModifier(preview2, new OmegaMod());
        MultiPreviewFieldPatches.addPreview(card, preview);
        //MultiPreviewFieldPatches.addPreview(card, preview2);
        InterruptUseCardFieldPatches.InterceptUseField.interceptUse.set(card, true);
        card.exhaust = true;
    }

    @Override
    public boolean validCard(AbstractCard card) {
        return (card.baseDamage > 0 || card.baseBlock > 0 || usesMagic(card));
    }

    @Override
    public String modifyName(String cardName, AbstractCard card) {
        return TEXT[0] + cardName + TEXT[1];
    }

    @Override
    public String modifyDescription(String rawDescription, AbstractCard card) {
        return String.format(TEXT[2], FormatHelper.prefixWords(card.name, "*"));
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
