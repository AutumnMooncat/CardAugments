package CardAugments.patches;

import CardAugments.cardmods.rare.SearingMod;
import basemod.abstracts.AbstractCardModifier;
import basemod.patches.com.megacrit.cardcrawl.cards.AbstractCard.CardModifierPatches;
import basemod.patches.com.megacrit.cardcrawl.saveAndContinue.SaveFile.ModSaves;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.ModInfo;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
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

public class InfiniteUpgradesPatches {
    @SpirePatch(clz = AbstractCard.class, method = SpirePatch.CLASS)
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
                            new SubclassClassFilter(AbstractCard.class),
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

    @SpirePatch2(clz = AbstractCard.class, method = "makeStatEquivalentCopy")
    public static class MakeStatEquivalentCopy {
        @SpireInsertPatch(locator = Locator.class, localvars = {"card"})
        public static void copyField(AbstractCard __instance, AbstractCard card) {
            if (InfUpgradeField.inf.get(__instance)) {
                InfUpgradeField.inf.set(card, true);
            }
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
    //Parse the cardmod gson (which has not yet loaded) to see if the card we are creating would have the Searing Mod
    @SpirePatch2(clz = CardLibrary.class, method = "getCopy", paramtypez = {String.class, int.class, int.class})
    public static class FixSaveAndLoadIssues {
        @SpireInsertPatch(locator = Locator.class, localvars = "retVal")
        public static void forceUpgrade(AbstractCard retVal) {
            GsonBuilder builder = new GsonBuilder();
            if (CardModifierPatches.modifierAdapter == null) {
                CardModifierPatches.initializeAdapterFactory();
            }

            builder.registerTypeAdapterFactory(CardModifierPatches.modifierAdapter);
            Gson gson = builder.create();
            ModSaves.ArrayListOfJsonElement cardModifierSaves = ModSaves.cardModifierSaves.get(CardCrawlGame.saveFile);
            int i = AbstractDungeon.player.masterDeck.size();
            ArrayList<AbstractCardModifier> cardModifiersList = gson.fromJson(cardModifierSaves != null && i < cardModifierSaves.size() ? cardModifierSaves.get(i) : null, (new TypeToken<ArrayList<AbstractCardModifier>>() {}).getType());
            if (cardModifiersList != null) {
                for (AbstractCardModifier mod : cardModifiersList) {
                    if (mod instanceof SearingMod) {
                        InfUpgradeField.inf.set(retVal, true);
                    }
                }
            }
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractCard.class, "upgrade");
                return new int[]{LineFinder.findInOrder(ctMethodToPatch, finalMatcher)[0]-1};
            }
        }
    }

    /*//First patch allows us to load cards that were saved with more upgrades than we can actually perform
    @SpirePatch2(clz = CardLibrary.class, method = "getCopy", paramtypez = {String.class, int.class, int.class})
    public static class FixSaveAndLoadIssues {
        private static boolean forcedUpgrade;
        @SpireInsertPatch(locator = Locator.class, localvars = "retVal")
        public static void forceUpgrade(AbstractCard retVal) {
            //If we still have upgrades to do but we cant upgrade, forcefully change the upgraded var so the upgrades are performed.
            //These will not correctly scale per upgrade, but it will create a card with the correct amount of upgrades
            if (!retVal.canUpgrade()) {
                forcedUpgrade = true;
                retVal.upgraded = false;
            }
        }
        @SpireInsertPatch(locator = LocatorAfter.class, localvars = "retVal")
        public static void fixName(AbstractCard retVal) {
            //Reset forced upgrade and correct the name which like likely something like Strike++++++
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

    //Second patch allows us to replace the card with a copy that correctly scales once card mods are loaded
    @SpirePatch2(clz = LoadPlayerSaves.class, method = "Postfix")
    public static class ReinitializeCards {
        @SpireInsertPatch(locator = Locator.class)
        public static void plz() {
            HashMap<AbstractCard, AbstractCard> replacements = new HashMap<>();
            for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
                //The card we got from loading has the correct upgrade amount and card mods, but no scaling was actually performed as the cardmods were not applied before the upgrades were done.
                if (InfUpgradeField.inf.get(c)) {
                    //Take a blank copy of the card with no upgrades
                    AbstractCard rawCopy = c.makeCopy();
                    //Set the field before upgrading
                    InfUpgradeField.inf.set(rawCopy, true);
                    //Perform the upgrades
                    for (int i = 0 ; i < c.timesUpgraded ; i++) {
                        rawCopy.upgrade();
                    }
                    //Reset the misc value
                    if (c.misc != 0) {
                        rawCopy.misc = c.misc;
                        if (rawCopy.cardID.equals("Genetic Algorithm")) {
                            rawCopy.block = rawCopy.misc;
                            rawCopy.baseBlock = rawCopy.misc;
                            rawCopy.initializeDescription();
                        }

                        if (rawCopy.cardID.equals("RitualDagger")) {
                            rawCopy.damage = rawCopy.misc;
                            rawCopy.baseDamage = rawCopy.misc;
                            rawCopy.initializeDescription();
                        }
                    }
                    //Attempt to unfuck custom saveable
                    if (c instanceof CustomSavableRaw && rawCopy instanceof CustomSavableRaw) {
                        ((CustomSavableRaw) rawCopy).onLoadRaw(((CustomSavableRaw) c).onSaveRaw());
                    }
                    //Copy over all the modifiers
                    CardModifierManager.copyModifiers(c, rawCopy, false, false, false);
                    //Attempt to unfuck bottles
                    //Modded bottle relics haven't loaded yet, so it should be okay?
                    if (c.inBottleLightning) {
                        rawCopy.inBottleLightning = true;
                        ((BottledLightning)AbstractDungeon.player.getRelic("Bottled Lightning")).card = rawCopy;
                        ((BottledLightning)AbstractDungeon.player.getRelic("Bottled Lightning")).setDescriptionAfterLoading();
                    }
                    if (c.inBottleFlame) {
                        rawCopy.inBottleFlame = true;
                        ((BottledFlame)AbstractDungeon.player.getRelic("Bottled Flame")).card = rawCopy;
                        ((BottledFlame)AbstractDungeon.player.getRelic("Bottled Flame")).setDescriptionAfterLoading();
                    }
                    if (c.inBottleTornado) {
                        rawCopy.inBottleTornado = true;
                        ((BottledTornado)AbstractDungeon.player.getRelic("Bottled Tornado")).card = rawCopy;
                        ((BottledTornado)AbstractDungeon.player.getRelic("Bottled Tornado")).setDescriptionAfterLoading();
                    }
                    replacements.put(c, rawCopy);
                }
            }
            //Replace the old cards with our newly rebuilt ones
            AbstractDungeon.player.masterDeck.group.replaceAll(c -> replacements.getOrDefault(c, c));
        }

        public static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                Matcher m = new Matcher.FieldAccessMatcher(ModSaves.class, "modRelicSaves");
                return LineFinder.findInOrder(ctBehavior, m);
            }
        }
    }*/

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
