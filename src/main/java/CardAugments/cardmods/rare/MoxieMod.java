package CardAugments.cardmods.rare;

import CardAugments.CardAugmentsMod;
import CardAugments.cardmods.AbstractAugment;
import CardAugments.damagemods.MoxieDamage;
import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import com.evacipated.cardcrawl.mod.stslib.damagemods.DamageModifierManager;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;

public class MoxieMod extends AbstractAugment {
    public static final String ID = CardAugmentsMod.makeID("MoxieMod");
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;

    private static final int BOOST = 2;

    @Override
    public void onInitialApplication(AbstractCard card) {
        DamageModifierManager.addModifier(card, new MoxieDamage(BOOST));
        card.exhaust = true;
    }

    @Override
    public boolean validCard(AbstractCard card) {
        return card.type == AbstractCard.CardType.ATTACK && card.baseDamage > 0 && cardDoesntExhaust(card);
    }

    @Override
    public String modifyName(String cardName, AbstractCard card) {
        return TEXT[0] + cardName + TEXT[1];
    }

    @Override
    public String modifyDescription(String rawDescription, AbstractCard card) {
        return rawDescription + String.format(TEXT[2], BOOST);
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.RARE;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new MoxieMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }

}
