package CardAugments.patches;

import CardAugments.cardmods.rare.SearingMod;
import basemod.abstracts.AbstractCardModifier;
import basemod.patches.com.megacrit.cardcrawl.cards.AbstractCard.CardModifierPatches;
import basemod.patches.com.megacrit.cardcrawl.saveAndContinue.SaveFile.ModSaves;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import javassist.expr.ExprEditor;
import javassist.expr.FieldAccess;

public class InfiniteUpgradesPatches {
    @SpirePatch(clz = AbstractCard.class, method = SpirePatch.CLASS)
    public static class InfUpgradeField {
        public static SpireField<Boolean> inf = new SpireField<>(() -> false);
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

    //Parse the cardmod gson (which has not yet loaded) to see if the card we are creating would have the Searing Mod
    @SpirePatch2(clz = CardLibrary.class, method = "getCopy", paramtypez = {String.class, int.class, int.class})
    public static class FixSaveAndLoadIssues {
        @SpireInsertPatch(locator = Locator.class, localvars = "retVal")
        public static void forceUpgrade(AbstractCard retVal) {
            GsonBuilder builder = new GsonBuilder();
            if (CardModifierPatches.modifierAdapter == null) {
                CardModifierPatches.initializeAdapterFactory();
            }

            if (ModSaves.cardModifierSaves != null && CardCrawlGame.saveFile != null) {
                builder.registerTypeAdapterFactory(CardModifierPatches.modifierAdapter);
                Gson gson = builder.create();
                ModSaves.ArrayListOfJsonElement cardModifierSaves = ModSaves.cardModifierSaves.get(CardCrawlGame.saveFile);
                int i = AbstractDungeon.player.masterDeck.size();
                if (cardModifierSaves != null) {
                    JsonElement loaded = i >= cardModifierSaves.size() ? null : cardModifierSaves.get(i);
                    if (loaded != null && loaded.isJsonArray()) {
                        JsonArray array = loaded.getAsJsonArray();
                        for (JsonElement element : array) {
                            AbstractCardModifier cardModifier = null;
                            try {
                                cardModifier = gson.fromJson(element, new TypeToken<AbstractCardModifier>() {}.getType());
                            } catch (Exception ignored) {}
                            if (cardModifier instanceof SearingMod) {
                                InfUpgradeField.inf.set(retVal, true);
                            }
                        }
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

    /*public static boolean renderCheck(AbstractCard card) {
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
    }*/
}
