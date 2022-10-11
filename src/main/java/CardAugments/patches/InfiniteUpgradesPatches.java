package CardAugments.patches;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import javassist.expr.ExprEditor;
import javassist.expr.FieldAccess;

public class InfiniteUpgradesPatches {
    @SpirePatch(clz = AbstractCard.class, method = "<class>")
    public static class InfUpgradeField {
        public static SpireField<Boolean> inf = new SpireField<>(() -> false);
    }

    @SpirePatch(clz = AbstractCard.class, method = "makeStatEquivalentCopy")
    public static class MakeStatEquivalentCopy {
        public static AbstractCard Postfix(AbstractCard result, AbstractCard self) {
            InfUpgradeField.inf.set(result, InfUpgradeField.inf.get(self));
            return result;
        }
    }

    @SpirePatch2(clz = AbstractCard.class, method = "canUpgrade")
    public static class BypassUpgradeLimit {
        @SpirePrefixPatch
        public static SpireReturn<?> plz(AbstractCard __instance) {
            if (InfUpgradeField.inf.get(__instance)) {
                __instance.upgraded = false;
                return SpireReturn.Return(true);
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch2(clz = AbstractCard.class, method = "upgradeDamage")
    @SpirePatch2(clz = AbstractCard.class, method = "upgradeBlock")
    @SpirePatch2(clz = AbstractCard.class, method = "upgradeMagicNumber")
    public static class ScaleStats {
        @SpirePrefixPatch
        public static void boost(AbstractCard __instance, @ByRef int[] amount) {
            if (InfUpgradeField.inf.get(__instance)) {
                amount[0] += Math.max(0, __instance.timesUpgraded-1);
            }
        }
    }

    @SpirePatch2(clz = AbstractCard.class, method = "upgradeName")
    public static class FixStackOfPlusSymbols {
        @SpireInsertPatch(locator = Locator.class)
        public static void plz(AbstractCard __instance) {
            if (InfUpgradeField.inf.get(__instance)) {
                __instance.upgraded = false;
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

    @SpirePatch2(clz = CardLibrary.class, method = "getCopy", paramtypez = {String.class, int.class, int.class})
    public static class FixSaveAndLoadIssues {
        private static boolean forcedUpgrade;
        @SpireInsertPatch(locator = Locator.class, localvars = "retVal")
        public static void forceUpgrade(AbstractCard retVal) {
            if (!retVal.canUpgrade()) {
                forcedUpgrade = true;
                retVal.upgraded = false;
            }
        }
        @SpireInsertPatch(locator = LocatorAfter.class, localvars = "retVal")
        public static void fixName(AbstractCard retVal) {
            if (forcedUpgrade) {
                forcedUpgrade = false;
                retVal.name = retVal.originalName + "+" + retVal.timesUpgraded;
            }
        }
        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractCard.class, "upgrade");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
        private static class LocatorAfter extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractCard.class, "upgrade");
                int[] ret = LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
                ret[0]++;
                return ret;
            }
        }
    }

    public static boolean renderCheck(AbstractCard card) {
        return InfUpgradeField.inf.get(card) && card.timesUpgraded > 0;
    }

    @SpirePatch2(clz = AbstractCard.class, method = "renderTitle")
    public static class FixRenderColor {
        @SpireInstrumentPatch
        public static ExprEditor patch() {
            return new ExprEditor() {
                @Override
                //Method call is basically the equivalent of a methodcallmatcher of an insert patch, checks the edit method against every method call in the function you#re patching
                public void edit(FieldAccess m) throws CannotCompileException {
                    //If the method is from the class AnimationState and the method is called update
                    if (m.getClassName().equals(AbstractCard.class.getName()) && m.getFieldName().equals("upgraded")) {
                        m.replace("$_ = CardAugments.patches.InfiniteUpgradesPatches.renderCheck($0) || $proceed($$);");
                    }
                }
            };
        }
    }
}
