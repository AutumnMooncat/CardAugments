package CardAugments.patches;

import CardAugments.cardmods.AbstractAugment;
import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.city.TheLibrary;
import com.megacrit.cardcrawl.events.shrines.GremlinMatchGame;
import com.megacrit.cardcrawl.neow.NeowEvent;
import com.megacrit.cardcrawl.screens.select.GridCardSelectScreen;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import javassist.CtBehavior;

import java.util.ArrayList;

public class CopyTheDamnModPatches {
    public static final ArrayList<AbstractCardModifier> modsToCopy = new ArrayList<>();
    public static boolean needsCopy;

    @SpirePatch2(clz = GremlinMatchGame.class, method = "updateMatchGameLogic")
    public static class ModifyMatchGameCards {
        @SpireInsertPatch(locator = Locator.class)
        public static void patch(AbstractCard ___chosenCard) {
            for (AbstractCardModifier m : CardModifierManager.modifiers(___chosenCard)) {
                if (m instanceof AbstractAugment) {
                    modsToCopy.add(m);
                }
            }
            needsCopy = true;
        }
        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(ArrayList.class, "add");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }

    @SpirePatch2(clz = ShowCardAndObtainEffect.class, method = SpirePatch.CONSTRUCTOR, paramtypez = {AbstractCard.class, float.class, float.class, boolean.class})
    public static class CopyTheMod {
        public static void Postfix(AbstractCard card) {
            if (needsCopy) {
                for (AbstractCardModifier m : modsToCopy) {
                    CardModifierManager.addModifier(card, m.makeCopy());
                }
                RolledModFieldPatches.RolledModField.rolled.set(card, true);
                needsCopy = false;
            }
            modsToCopy.clear();
        }
    }

    @SpirePatch2(clz = TheLibrary.class, method = "update")
    public static class FixLibraryCopy {
        @SpireInsertPatch(locator = Locator.class, localvars = "c")
        public static void patch(AbstractCard c) {
            for (AbstractCardModifier m : CardModifierManager.modifiers(AbstractDungeon.gridSelectScreen.selectedCards.get(0))) {
                if (m instanceof AbstractAugment) {
                    CardModifierManager.addModifier(c, m.makeCopy());
                }
            }
            RolledModFieldPatches.RolledModField.rolled.set(c, RolledModFieldPatches.RolledModField.rolled.get(AbstractDungeon.gridSelectScreen.selectedCards.get(0)));
        }
        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(ArrayList.class, "add");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }

    @SpirePatch2(clz = NeowEvent.class, method = "update")
    public static class StopTossingMods {
        @SpireInsertPatch(locator = Locator.class, localvars = "group")
        public static void plz(CardGroup group) {
            for (int i = 0 ; i < AbstractDungeon.gridSelectScreen.selectedCards.size() ; i++) {
                for (AbstractCardModifier mod : CardModifierManager.modifiers(AbstractDungeon.gridSelectScreen.selectedCards.get(i))) {
                    if (mod instanceof AbstractAugment) {
                        CardModifierManager.addModifier(group.group.get(group.group.size()-1-i), mod.makeCopy());
                    }
                }
                //Don't let it try to roll on instant obtain, since these are starter cards
                RolledModFieldPatches.RolledModField.rolled.set(group.group.get(group.group.size()-1-i), true);
            }
        }

        public static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                Matcher m = new Matcher.MethodCallMatcher(GridCardSelectScreen.class, "openConfirmationGrid");
                return LineFinder.findInOrder(ctBehavior, m);
            }
        }
    }
}
