package CardAugments.patches;

import CardAugments.CardAugmentsMod;
import CardAugments.cardmods.AbstractAugment;
import basemod.abstracts.AbstractCardModifier;
import basemod.abstracts.DynamicVariable;
import basemod.helpers.CardModifierManager;
import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;

public class ModVar extends DynamicVariable {

    @Override
    public String key() {
        return CardAugmentsMod.makeID("V");
    }

    @Override
    public boolean isModified(AbstractCard card) {
        return getModified(card);
    }

    @Override
    public int value(AbstractCard card) {
        return getVal(card);
    }

    @Override
    public int baseValue(AbstractCard card) {
        return getBaseVal(card);
    }

    @Override
    public boolean upgraded(AbstractCard card) {
        for (AbstractCardModifier m : CardModifierManager.modifiers(card)) {
            if (m instanceof AbstractAugment) {
                ((AbstractAugment) m).onUpgradeCheck(card);
            }
        }
        return getUpgraded(card);
    }

    @SpirePatch(clz = AbstractCard.class, method = "<class>")
    public static class ModDamageFields {
        public static SpireField<Integer> baseValue = new SpireField<>(() -> -1);
        public static SpireField<Integer> value = new SpireField<>(() -> -1);
        public static SpireField<Boolean> isValueModified = new SpireField<>(() -> false);
        public static SpireField<Boolean> isValueUpgraded = new SpireField<>(() -> false);
    }

    public static void setVal(AbstractCard card, int val) {
        ModDamageFields.value.set(card, val);
    }

    public static void setBaseVal(AbstractCard card, int val) {
        ModDamageFields.baseValue.set(card, val);
    }

    public static void setModified(AbstractCard card, boolean val) {
        ModDamageFields.isValueModified.set(card, val);
    }

    public static void updateModified(AbstractCard card) {
        ModDamageFields.isValueModified.set(card, getVal(card) != getBaseVal(card));
    }

    public static void setUpgraded(AbstractCard card, boolean val) {
        ModDamageFields.isValueUpgraded.set(card, val);
    }

    public static int getVal(AbstractCard card) {
        return ModDamageFields.value.get(card);
    }

    public static int getBaseVal(AbstractCard card) {
        return ModDamageFields.baseValue.get(card);
    }

    public static boolean getModified(AbstractCard card) {
        return ModDamageFields.isValueModified.get(card);
    }

    public static boolean getUpgraded(AbstractCard card) {
        return ModDamageFields.isValueUpgraded.get(card);
    }
}
