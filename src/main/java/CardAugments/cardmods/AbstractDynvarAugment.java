package CardAugments.cardmods;

import CardAugments.util.DynamicDynamicVariableManager;
import com.megacrit.cardcrawl.cards.AbstractCard;

public abstract class AbstractDynvarAugment extends AbstractAugment {
    public boolean isValueModified;
    public int value;
    public int baseValue;
    protected String key;

    @Override
    public void onInitialApplication(AbstractCard card) {
        DynamicDynamicVariableManager.registerVariable(card, this);
        key = "!" + DynamicDynamicVariableManager.generateKey(card, this) + "!";
    }

    public boolean isModified(AbstractCard card) {
        return isValueModified;
    }

    public int value(AbstractCard card) {
        return value;
    }

    public int baseValue(AbstractCard card) {
        return baseValue;
    }

    public boolean upgraded(AbstractCard card) {
        return false;
    }

    public void updateDynvar(AbstractCard card) {}

    public abstract boolean shouldRenderValue();
}
