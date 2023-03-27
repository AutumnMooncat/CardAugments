package CardAugments.patches;

import CardAugments.CardAugmentsMod;
import CardAugments.screens.ModifierScreen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.screens.mainMenu.MainMenuScreen;
import com.megacrit.cardcrawl.screens.mainMenu.MenuButton;
import com.megacrit.cardcrawl.screens.mainMenu.SaveSlotScreen;
import javassist.CtBehavior;

public class MainMenuPatches {
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(CardAugmentsMod.makeID("MainMenu"));
    public static final String[] TEXT = uiStrings.TEXT;

    public static class Enums {
        @SpireEnum
        public static MenuButton.ClickResult MODIFIERS_BUTTON;
        @SpireEnum
        public static MainMenuScreen.CurScreen MODIFIERS_VIEW;
    }

    @SpirePatch(clz = MainMenuScreen.class, method = SpirePatch.CLASS)
    public static class ModifierScreenField {
        public static SpireField<ModifierScreen> modifierScreen = new SpireField<>(() -> null);
    }

    @SpirePatch2(clz = MainMenuScreen.class, method = "setMainMenuButtons")
    public static class ButtonAdderPatch {
        @SpireInsertPatch(locator= ButtonLocator.class, localvars={"index"})
        public static void setMainMenuButtons(MainMenuScreen __instance, @ByRef int[] index) {
            __instance.buttons.add(new MenuButton(Enums.MODIFIERS_BUTTON, index[0]));
            index[0]++;
        }

        private static class ButtonLocator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(Settings.class, "isShowBuild");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }

    @SpirePatch2(clz = MenuButton.class, method = "setLabel")
    public static class SetText {
        @SpirePostfixPatch
        public static void setLobelText(MenuButton __instance, @ByRef String[] ___label) {
            if (__instance.result == Enums.MODIFIERS_BUTTON) {
                ___label[0] = TEXT[0];
            }
        }
    }

    @SpirePatch2(clz = MenuButton.class, method = "buttonEffect")
    public static class OnClickButton {
        @SpirePostfixPatch
        public static void openScreen(MenuButton __instance) {
            if (__instance.result == Enums.MODIFIERS_BUTTON) {
                ModifierScreenField.modifierScreen.get(CardCrawlGame.mainMenuScreen).open();
            }
        }
    }

    @SpirePatch2(clz = MainMenuScreen.class, method = "<ctor>", paramtypez = {boolean.class})
    private static class AddNewScreenToSpireField {
        @SpirePostfixPatch()
        public static void screenTime(MainMenuScreen __instance) {
            ModifierScreenField.modifierScreen.set(__instance, new ModifierScreen());
        }
    }

    @SpirePatch2(clz = MainMenuScreen.class, method = "update")
    public static class UpdateModifierScreen {
        @SpireInsertPatch(locator= UpdateLocator.class)
        public static void updateTime(MainMenuScreen __instance) {
            if (__instance.screen == Enums.MODIFIERS_VIEW) {
                ModifierScreenField.modifierScreen.get(__instance).update();
            }
        }

        private static class UpdateLocator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(SaveSlotScreen.class, "update");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }

    @SpirePatch2(clz = MainMenuScreen.class, method = "render")
    public static class RenderModifierScreen {
        @SpireInsertPatch(locator= RenderLocator.class)
        public static void renderTime(MainMenuScreen __instance, SpriteBatch sb) {
            if (__instance.screen == Enums.MODIFIERS_VIEW) {
                ModifierScreenField.modifierScreen.get(__instance).render(sb);
            }
        }

        private static class RenderLocator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(SaveSlotScreen.class, "render");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }
}
