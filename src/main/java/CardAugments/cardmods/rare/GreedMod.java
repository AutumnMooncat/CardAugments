package CardAugments.cardmods.rare;

import CardAugments.CardAugmentsMod;
import CardAugments.cardmods.AbstractAugment;
import CardAugments.damagemods.GreedDamage;
import basemod.abstracts.AbstractCardModifier;
import com.evacipated.cardcrawl.mod.stslib.damagemods.DamageModifierManager;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;

public class GreedMod extends AbstractAugment {
    public static final String ID = CardAugmentsMod.makeID("GreedMod");
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;

    private static final int GOLD = 15;

    @Override
    public void onInitialApplication(AbstractCard card) {
        DamageModifierManager.addModifier(card, new GreedDamage(GOLD));
        card.exhaust = true;
    }

    @Override
    public boolean validCard(AbstractCard card) {
        return card.type == AbstractCard.CardType.ATTACK && card.baseDamage > 0 && cardCheck(card, AbstractAugment::notExhaust);
    }

    @Override
    public String modifyName(String cardName, AbstractCard card) {
        String[] nameParts = removeUpgradeText(cardName);
        return TEXT[0] + nameParts[0] + TEXT[1] + nameParts[1];
    }

    @Override
    public String modifyDescription(String rawDescription, AbstractCard card) {
        return rawDescription + String.format(TEXT[2], GOLD);
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.RARE;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new GreedMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }

}
