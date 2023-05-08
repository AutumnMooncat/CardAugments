package CardAugments.cardmods.uncommon;

import CardAugments.CardAugmentsMod;
import CardAugments.cardmods.AbstractAugment;
import basemod.abstracts.AbstractCardModifier;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class EndlessMod extends AbstractAugment {
    public static final String ID = CardAugmentsMod.makeID(EndlessMod.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;
    public static final String[] CARD_TEXT = CardCrawlGame.languagePack.getUIString(ID).EXTRA_TEXT;

    private boolean setExhaust;

    @Override
    public void onInitialApplication(AbstractCard card) {
        if (card.type != AbstractCard.CardType.POWER && !card.exhaust) {
            card.exhaust = true;
            setExhaust = true;
        }
    }

    @Override
    public float modifyBaseDamage(float damage, DamageInfo.DamageType type, AbstractCard card, AbstractMonster target) {
        if (card.baseDamage > 1) {
            return damage * MINOR_DEBUFF;
        }
        return damage;
    }

    @Override
    public float modifyBaseBlock(float block, AbstractCard card) {
        if (card.baseBlock > 1) {
            return block * MINOR_DEBUFF;
        }
        return block;
    }

    @Override
    public boolean validCard(AbstractCard card) {
        return isNormalCard(card) && cardCheck(card, c -> doesntUpgradeExhaust()) && card.cost == 0 && (card.baseDamage > 1 || card.baseBlock > 1);
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
        if (rawDescription.contains(CARD_TEXT[3])) {
            return rawDescription.replace(CARD_TEXT[3], CARD_TEXT[4]);
        }
        return insertAfterText(rawDescription, CARD_TEXT[0]) + (setExhaust ? CARD_TEXT[1] : "");
    }

    @Override
    public void onDrawn(AbstractCard card) {
        this.addToBot(new MakeTempCardInHandAction(card.makeStatEquivalentCopy()));
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.UNCOMMON;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new EndlessMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}
