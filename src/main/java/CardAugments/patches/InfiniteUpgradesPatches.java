package CardAugments.patches;

import basemod.helpers.CardModifierManager;
import basemod.patches.com.megacrit.cardcrawl.core.CardCrawlGame.LoadPlayerSaves;
import basemod.patches.com.megacrit.cardcrawl.saveAndContinue.SaveFile.ModSaves;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.ModInfo;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import javassist.*;
import javassist.expr.ExprEditor;
import javassist.expr.FieldAccess;
import org.clapper.util.classutil.*;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;

public class InfiniteUpgradesPatches {
    @SpirePatch(clz = AbstractCard.class, method = "<class>")
    public static class InfUpgradeField {
        public static SpireField<Boolean> inf = new SpireField<>(() -> false);
    }

    @SpirePatch(clz = CardCrawlGame.class, method = SpirePatch.CONSTRUCTOR)
    public static class UpgradePatch {
        @SpireRawPatch
        public static void resetUpgraded(CtBehavior ctBehavior) throws NotFoundException {
            ClassFinder finder = new ClassFinder();
            finder.add(new File(Loader.STS_JAR));

            for (ModInfo modInfo : Loader.MODINFOS) {
                if (modInfo.jarURL != null) {
                    try {
                        finder.add(new File(modInfo.jarURL.toURI()));
                    } catch (URISyntaxException ignored) {}
                }
            }

            ClassFilter filter = new AndClassFilter(
                    new NotClassFilter(new InterfaceOnlyClassFilter()),
                    new ClassModifiersClassFilter(Modifier.PUBLIC),
                    new OrClassFilter(
                            new org.clapper.util.classutil.SubclassClassFilter(AbstractCard.class),
                            (classInfo, classFinder) -> classInfo.getClassName().equals(AbstractCard.class.getName())
                    )
            );

            ArrayList<ClassInfo> foundClasses = new ArrayList<>();
            finder.findClasses(foundClasses, filter);

            for (ClassInfo classInfo : foundClasses) {
                CtClass ctClass = ctBehavior.getDeclaringClass().getClassPool().get(classInfo.getClassName());
                try {
                    CtMethod[] methods = ctClass.getDeclaredMethods();
                    for (CtMethod m : methods) {
                        if (m.getName().equals("upgrade")) {
                            m.insertBefore(InfiniteUpgradesPatches.class.getName() + ".infCheck($0);");
                        }
                    }
                } catch (CannotCompileException ignored) {}
            }
        }
    }

    public static void infCheck(AbstractCard card) {
        if (InfUpgradeField.inf.get(card)) {
            card.upgraded = false;
        }
    }

    @SpirePatch2(clz = LoadPlayerSaves.class, method = "Postfix")
    public static class ReinitializeCards {
        @SpireInsertPatch(locator = Locator.class)
        public static void plz() {
            HashMap<AbstractCard, AbstractCard> replacements = new HashMap<>();
            for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
                if (InfUpgradeField.inf.get(c)) {
                    AbstractCard rawCopy = c.makeCopy();
                    CardModifierManager.copyModifiers(c, rawCopy, false, false, false);
                    for (int i = 0 ; i < c.timesUpgraded ; i++) {
                        rawCopy.upgrade();
                    }
                    replacements.put(c, rawCopy.makeStatEquivalentCopy());
                }
            }
            AbstractDungeon.player.masterDeck.group.replaceAll(c -> replacements.getOrDefault(c, c));
        }

        public static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                Matcher m = new Matcher.FieldAccessMatcher(ModSaves.class, "modRelicSaves");
                return LineFinder.findInOrder(ctBehavior, m);
            }
        }
    }

    @SpirePatch2(clz = AbstractCard.class, method = "makeStatEquivalentCopy")
    public static class MakeStatEquivalentCopy {
        @SpireInsertPatch(locator = Locator.class, localvars = {"card"})
        public static void copyField(AbstractCard __instance, AbstractCard card) {
            InfiniteUpgradesPatches.InfUpgradeField.inf.set(card, true);
        }

        public static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                Matcher m = new Matcher.FieldAccessMatcher(AbstractCard.class, "timesUpgraded");
                return LineFinder.findInOrder(ctBehavior, m);
            }
        }
    }

    @SpirePatch2(clz = AbstractCard.class, method = "canUpgrade")
    public static class BypassUpgradeLimit {
        @SpirePrefixPatch
        public static SpireReturn<?> plz(AbstractCard __instance) {
            if (InfUpgradeField.inf.get(__instance)) {
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
