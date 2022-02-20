package CardAugments.patches;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import javassist.CtBehavior;

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
}
