package CardAugments.cardmods.rare;

import CardAugments.CardAugmentsMod;
import CardAugments.cardmods.AbstractAugment;
import basemod.abstracts.AbstractCardModifier;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class BludgeonMod extends AbstractAugment {
    public static final String ID = CardAugmentsMod.makeID(BludgeonMod.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;

    private boolean wasZero;

    @Override
    public void onInitialApplication(AbstractCard card) {
        wasZero = card.cost == 0;
        card.cost = 3;
        card.costForTurn = 3;
    }

    @Override
    public float modifyBaseDamage(float damage, DamageInfo.DamageType type, AbstractCard card, AbstractMonster target) {
        return wasZero ? damage * 5f : damage * 3.5f;
    }

    @Override
    public boolean validCard(AbstractCard card) {
        return (card.cost == 1 || card.cost == 0) && card.baseDamage > 0 && cardCheck(card, c -> doesntUpgradeCost());
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
    public AugmentRarity getModRarity() {
        return AugmentRarity.RARE;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new BludgeonMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}
