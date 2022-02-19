//package CardAugments.util;
//
//import CardAugments.CardAugmentsMod;
//import CardAugments.cardmods.AbstractDynvarAugment;
//import basemod.BaseMod;
//import basemod.abstracts.DynamicVariable;
//import com.badlogic.gdx.graphics.Color;
//import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
//import com.megacrit.cardcrawl.cards.AbstractCard;
//import com.megacrit.cardcrawl.core.Settings;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.HashMap;
//
//public class DynamicDynamicVariableManager extends DynamicVariable {
//    public static DynamicDynamicVariableManager managerInstance;
//    private static final Color normalColor = Settings.CREAM_COLOR; //Color.valueOf("65ada1");
//    private static final Color increasedValueColor = Settings.GREEN_TEXT_COLOR; //Color.valueOf("9ad6b6");
//    private static final Color decreasedValueColor = Settings.RED_TEXT_COLOR; //Color.valueOf("b799bd");
//    public HashMap<AbstractCard, ArrayList<AbstractDynvarAugment>> variableDatabase;
//    private int timesKeyFoundThisFrame = -1;
//
//    @Override
//    public String key() {
//        managerInstance = this;
//        managerInstance.variableDatabase = new HashMap<>();
//        return CardAugmentsMod.makeID("dynamicdynamic"); //ideally this should never actually be used
//    }
//
//    @Override
//    public boolean isModified(AbstractCard card) {
//        AbstractDynvarAugment mod;
//        do {
//            ++timesKeyFoundThisFrame;
//            mod = variableDatabase.get(card).get(timesKeyFoundThisFrame);
//        } while (!mod.shouldRenderValue());
//        return mod.isModified(card);
//    }
//
//    @Override
//    public int value(AbstractCard card) {
//        if (timesKeyFoundThisFrame == -1) {
//            return 0; //prevent a crash
//        }
//        AbstractDynvarAugment mod = variableDatabase.get(card).get(timesKeyFoundThisFrame);
//        return mod.value(card);
//    }
//
//    @Override
//    public int baseValue(AbstractCard card) {
//        if (timesKeyFoundThisFrame == -1) {
//            return 0; //prevent a crash
//        }
//        AbstractDynvarAugment mod = variableDatabase.get(card).get(timesKeyFoundThisFrame);
//        return mod.baseValue(card);
//    }
//
//    @Override
//    public Color getNormalColor() {
//        if (timesKeyFoundThisFrame == -1) {
//            return Color.BLACK.cpy();
//        }
//        return normalColor;
//    }
//
//    @Override
//    public Color getIncreasedValueColor() {
//        if (timesKeyFoundThisFrame == -1) {
//            return Color.BLACK.cpy();
//        }
//        return increasedValueColor;
//    }
//
//    @Override
//    public Color getDecreasedValueColor() {
//        if (timesKeyFoundThisFrame == -1) {
//            return Color.BLACK.cpy();
//        }
//        return decreasedValueColor;
//    }
//
//    @Override
//    public boolean upgraded(AbstractCard card) {
//        if (timesKeyFoundThisFrame == -1) {
//            return false; //prevent a crash
//        }
//        AbstractDynvarAugment mod = variableDatabase.get(card).get(timesKeyFoundThisFrame);
//        return mod.onUpgradeCheck(card);
//    }
//
//    public static void resetTicker() {
//        managerInstance.timesKeyFoundThisFrame = -1;
//    }
//
//    public static void clearVariables() {
//        for (AbstractCard card : managerInstance.variableDatabase.keySet()) {
//            ArrayList<AbstractDynvarAugment> list = managerInstance.variableDatabase.get(card);
//            for (AbstractDynvarAugment mod : list) {
//                BaseMod.cardDynamicVariableMap.remove(generateKey(card, mod));
//            }
//        }
//        managerInstance.variableDatabase.clear();
//    }
//
//    public static void registerVariable(AbstractCard card, AbstractDynvarAugment mod) {
//        if (!managerInstance.variableDatabase.containsKey(card)) {
//            ArrayList<AbstractDynvarAugment> list = new ArrayList<>();
//            list.add(mod);
//            managerInstance.variableDatabase.put(card, list);
//        } else {
//            ArrayList<AbstractDynvarAugment> list = managerInstance.variableDatabase.get(card);
//            if (list.contains(mod)) {
//                return;
//            }
//            list.add(mod);
//            Collections.sort(list);
//        }
//        BaseMod.cardDynamicVariableMap.put(generateKey(card, mod), managerInstance);
//    }
//
//    public static String generateKey(AbstractCard card, AbstractDynvarAugment mod) {
//        return CardAugmentsMod.makeID(card.uuid + ":" + managerInstance.variableDatabase.get(card).size());
//    }
//
//    @SpirePatch2(clz = AbstractCard.class, method = "renderDescription")
//    public static class ResetDynamicVariableTickerPowersPatch {
//        public static void Prefix() {
//            resetTicker();
//        }
//    }
//}