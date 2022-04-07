package CardAugments.patches;

import basemod.devcommands.deck.DeckAdd;
import basemod.devcommands.hand.HandAdd;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.cards.AbstractCard;
import javassist.CtBehavior;

import java.util.ArrayList;

public class ConsoleCommandStopAddingModsPatches {
    @SpirePatch2(clz = HandAdd.class, method = "execute")
    public static class stopRollingModsOnSpawnedCardsPlz {
        @SpireInsertPatch(locator = Locator.class, localvars = "copy")
        public static void patch(AbstractCard copy) {
            RolledModFieldPatches.RolledModField.rolled.set(copy, true);
        }
        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(GameActionManager.class, "addToBottom");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }

    @SpirePatch2(clz = DeckAdd.class, method = "execute")
    public static class stopRollingModsOnSpawnedCardsPlz2 {
        @SpireInsertPatch(locator = Locator.class, localvars = "copy")
        public static void patch(AbstractCard copy) {
            RolledModFieldPatches.RolledModField.rolled.set(copy, true);
        }
        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(ArrayList.class, "add");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }
}
