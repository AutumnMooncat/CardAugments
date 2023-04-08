package CardAugments.patches;

import CardAugments.cardmods.AbstractAugment;
import CardAugments.cardmods.rare.JankMod;
import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import javassist.CtBehavior;

public class OnUpgradePatches {
    public static void onUpgrade(AbstractCard card) {
        for (AbstractCardModifier m : CardModifierManager.modifiers(card)) {
            if (m instanceof AbstractAugment) {
                ((AbstractAugment) m).onUpgradeCheck(card);
            }
        }
    }

    @SpirePatch2(clz = AbstractCard.class, method = "upgradeDamage")
    public static class DamageUpgrade {
        @SpirePrefixPatch
        public static SpireReturn<?> boost(AbstractCard __instance, @ByRef int[] amount) {
            if (InfiniteUpgradesPatches.InfUpgradeField.inf.get(__instance)) {
                amount[0] += Math.max(0, __instance.timesUpgraded-1);
            }
            if (CardModifierManager.hasModifier(__instance, JankMod.ID)) {
                JankMod jm = (JankMod) CardModifierManager.getModifiers(__instance, JankMod.ID).get(0);
                jm.upDamage = amount[0];
                return SpireReturn.Return();
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch2(clz = AbstractCard.class, method = "upgradeBlock")
    public static class BlockUpgrade {
        @SpirePrefixPatch
        public static SpireReturn<?> boost(AbstractCard __instance, @ByRef int[] amount) {
            if (InfiniteUpgradesPatches.InfUpgradeField.inf.get(__instance)) {
                amount[0] += Math.max(0, __instance.timesUpgraded-1);
            }
            if (CardModifierManager.hasModifier(__instance, JankMod.ID)) {
                JankMod jm = (JankMod) CardModifierManager.getModifiers(__instance, JankMod.ID).get(0);
                jm.upBlock = amount[0];
                return SpireReturn.Return();
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch2(clz = AbstractCard.class, method = "upgradeMagicNumber")
    public static class MagicUpgrade {
        @SpirePrefixPatch
        public static SpireReturn<?> boost(AbstractCard __instance, @ByRef int[] amount) {
            if (InfiniteUpgradesPatches.InfUpgradeField.inf.get(__instance)) {
                amount[0] += Math.max(0, __instance.timesUpgraded-1);
            }
            if (CardModifierManager.hasModifier(__instance, JankMod.ID)) {
                JankMod jm = (JankMod) CardModifierManager.getModifiers(__instance, JankMod.ID).get(0);
                jm.upMagic = amount[0];
                return SpireReturn.Return();
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch2(clz = AbstractCard.class, method = "upgradeName")
    public static class FixStackOfPlusSymbols {
        @SpireInsertPatch(locator = Locator.class)
        public static void plz(AbstractCard __instance) {
            if (InfiniteUpgradesPatches.InfUpgradeField.inf.get(__instance)) {
                __instance.name = __instance.originalName + "+" + __instance.timesUpgraded;
            }
        }
        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractCard.class, "initializeTitle");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }
}
