package CardAugments.patches;

import CardAugments.cardmods.AbstractDynvarAugment;
import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;

public class UpdateDynvarValuesPatches {
//    @SpirePatch2(clz = CampfireSmithEffect.class, method = "update")
//    public static class InitDescriptionOnUpgrade {
//        @SpireInsertPatch(locator = Locator.class)
//        public static void patch() {
//            for (AbstractCard c : AbstractDungeon.gridSelectScreen.selectedCards) {
//                c.initializeDescription();
//            }
//        }
//        private static class Locator extends SpireInsertLocator {
//            @Override
//            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
//                Matcher finalMatcher = new Matcher.MethodCallMatcher(ArrayList.class, "clear");
//                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
//            }
//        }
//    }

    @SpirePatch2(clz = AbstractPlayer.class, method = "bottledCardUpgradeCheck")
    public static class FixDynVarModsOnUpgrade {
        @SpirePostfixPatch
        public static void plz(AbstractCard c) {
            for (AbstractCardModifier m : CardModifierManager.modifiers(c)) {
                if (m instanceof AbstractDynvarAugment) {
                    ((AbstractDynvarAugment) m).updateDynvar(c);
                }
            }
        }
    }

    @SpirePatch2(clz = AbstractCard.class, method = "resetAttributes")
    public static class FixDynVarModsWhenResettingVars {
        @SpirePostfixPatch
        public static void plz(AbstractCard __instance) {
            for (AbstractCardModifier m : CardModifierManager.modifiers(__instance)) {
                if (m instanceof AbstractDynvarAugment) {
                    ((AbstractDynvarAugment) m).updateDynvar(__instance);
                }
            }
        }
    }
}
