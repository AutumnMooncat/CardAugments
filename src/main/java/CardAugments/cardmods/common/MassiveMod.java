package CardAugments.cardmods.common;

import CardAugments.CardAugmentsMod;
import CardAugments.cardmods.AbstractAugment;
import basemod.abstracts.AbstractCardModifier;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class MassiveMod extends AbstractAugment {
    public static final String ID = CardAugmentsMod.makeID(MassiveMod.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;

    private int baseCost;

    @Override
    public void onInitialApplication(AbstractCard card) {
        baseCost = Math.max(1, card.cost);
        card.cost = card.cost + 1;
        card.costForTurn = card.cost;
    }

    @Override
    public float modifyBaseDamage(float damage, DamageInfo.DamageType type, AbstractCard card, AbstractMonster target) {
        if (card.baseDamage > 0) {
            return damage * (baseCost+1f)/baseCost;
        }
        return damage;
    }

    @Override
    public float modifyBaseBlock(float block, AbstractCard card) {
        if (card.baseBlock > 0) {
            return block * (baseCost+1f)/baseCost;
        }
        return block;
    }

    @Override
    public boolean validCard(AbstractCard card) {
        return card.cost >= 0 && (card.baseDamage > 0 || card.baseBlock > 0) && cardCheck(card, c -> doesntUpgradeCost());
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
        return AugmentRarity.COMMON;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new MassiveMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}
