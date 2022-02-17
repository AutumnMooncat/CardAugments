package CardAugments.cardmods;

import basemod.abstracts.AbstractCardModifier;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public abstract class AbstractAugment extends AbstractCardModifier {
    public enum AugmentRarity {
        COMMON,
        UNCOMMON,
        RARE
    }

    public enum BuffType {
        DAMAGE,
        BLOCK,
        MAGIC
    }

    public enum BuffScale {
        MAJOR_BUFF,
        MODERATE_BUFF,
        MINOR_BUFF,
        MINOR_DEBUFF,
        MODERATE_DEBUFF,
        MAJOR_DEBUFF,
    }

    public void modifyBaseStat(AbstractCard card, BuffType type, BuffScale scaling) {
        AbstractCard upgradeCheck = card.makeCopy();
        upgradeCheck.upgrade();
        switch (type) {
            case DAMAGE:
                card.baseDamage += getStatModification(Math.max(card.baseDamage, upgradeCheck.baseDamage), scaling);
                if (card.baseDamage < 1) {
                    card.baseDamage = 1;
                }
                card.damage = card.baseDamage;
                break;
            case BLOCK:
                card.baseBlock += getStatModification(Math.max(card.baseBlock, upgradeCheck.baseBlock), scaling);
                if (card.baseBlock < 1) {
                    card.baseBlock = 1;
                }
                card.block = card.baseBlock;
                break;
            case MAGIC:
                card.baseMagicNumber += getStatModification(Math.max(card.baseMagicNumber, upgradeCheck.baseMagicNumber), scaling);
                if (card.baseMagicNumber < 1) {
                    card.baseMagicNumber = 1;
                }
                card.magicNumber = card.baseMagicNumber;
                break;
        }
    }

    public int getStatModification(int baseStat, BuffScale scaling) {
        switch (scaling) {
            case MAJOR_BUFF:
                return (int) (Math.ceil(baseStat/3f) + 1);
            case MODERATE_BUFF:
                return (int) Math.ceil(baseStat/3f);
            case MINOR_BUFF:
                return (int) Math.ceil(baseStat/5f);
            case MINOR_DEBUFF:
                return (int) -Math.ceil(baseStat/5f);
            case MODERATE_DEBUFF:
                return (int) -Math.ceil(baseStat/3f);
            case MAJOR_DEBUFF:
                return (int) -(Math.ceil(baseStat/3f) + 1);
        }
        return 0;
    }

    public abstract AugmentRarity getModRarity();

    public abstract boolean validCard(AbstractCard card);

    @Override
    public boolean shouldApply(AbstractCard card) {
        if (!validCard(card)) {
            return false;
        }
        AbstractCard upgradeCheck = card.makeCopy();
        upgradeCheck.upgrade();
        return validCard(upgradeCheck);
    }

    protected void addToBot(AbstractGameAction action) {
        AbstractDungeon.actionManager.addToBottom(action);
    }

    protected void addToTop(AbstractGameAction action) {
        AbstractDungeon.actionManager.addToTop(action);
    }

    protected static boolean isNormalCard(AbstractCard card) {
        return card.type == AbstractCard.CardType.ATTACK || card.type == AbstractCard.CardType.SKILL || card.type == AbstractCard.CardType.POWER;
    }
}
