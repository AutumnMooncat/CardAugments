package CardAugments.cardmods;

import CardAugments.patches.ChangeRenderedNamePatches;
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

    @Override
    public void onInitialApplication(AbstractCard card) {
        setPrefixSuffix(card);
    }

    public void setPrefixSuffix(AbstractCard card) {
        ChangeRenderedNamePatches.PrefixSuffixFields.prefix.set(card, getPrefix() + ChangeRenderedNamePatches.PrefixSuffixFields.prefix.get(card));
        ChangeRenderedNamePatches.PrefixSuffixFields.suffix.set(card, ChangeRenderedNamePatches.PrefixSuffixFields.suffix.get(card) + getSuffix());
    }

    public String getPrefix() {
        return "";
    }

    public String getSuffix() {
        return "";
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
