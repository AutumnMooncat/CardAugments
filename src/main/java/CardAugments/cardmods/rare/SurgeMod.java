package CardAugments.cardmods.rare;

import CardAugments.CardAugmentsMod;
import CardAugments.cardmods.AbstractAugment;
import basemod.abstracts.AbstractCardModifier;
import com.evacipated.cardcrawl.mod.stslib.fields.cards.AbstractCard.ExhaustiveField;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.ArtifactPower;

public class SurgeMod extends AbstractAugment {
    public static final String ID = CardAugmentsMod.makeID("SurgeMod");
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;

    private static final int AMOUNT = 1;

    @Override
    public boolean validCard(AbstractCard card) {
        return card.rarity != AbstractCard.CardRarity.COMMON && card.cost != -2 && isNormalCard(card) && !card.purgeOnUse && !card.exhaust && card.type != AbstractCard.CardType.POWER && (ExhaustiveField.ExhaustiveFields.baseExhaustive.get(card) == -1 || ExhaustiveField.ExhaustiveFields.exhaustive.get(card) == -1);
    }

    @Override
    public String modifyName(String cardName, AbstractCard card) {
        return TEXT[0] + cardName + TEXT[1];
    }

    @Override
    public String modifyDescription(String rawDescription, AbstractCard card) {
        return rawDescription + String.format(TEXT[2], AMOUNT);
    }

    @Override
    public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
        this.addToBot(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new ArtifactPower(AbstractDungeon.player, AMOUNT)));
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.RARE;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new SurgeMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}
