package CardAugments.patches;

import CardAugments.CardAugmentsMod;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.city.TheLibrary;
import com.megacrit.cardcrawl.events.shrines.GremlinMatchGame;
import com.megacrit.cardcrawl.neow.NeowEvent;
import com.megacrit.cardcrawl.neow.NeowReward;
import com.megacrit.cardcrawl.relics.PandorasBox;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.screens.CardRewardScreen;
import com.megacrit.cardcrawl.screens.CombatRewardScreen;
import com.megacrit.cardcrawl.screens.select.GridCardSelectScreen;
import com.megacrit.cardcrawl.shop.Merchant;
import com.megacrit.cardcrawl.shop.ShopScreen;
import com.megacrit.cardcrawl.vfx.FastCardObtainEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import javassist.CtBehavior;

import java.util.ArrayList;

import static CardAugments.CardAugmentsMod.modifyStarters;
import static CardAugments.CardAugmentsMod.rollCardAugment;

public class OnCardGeneratedPatches {

    @SpirePatch2(clz = CombatRewardScreen.class, method = "setupItemReward")
    public static class ModifyRewardScreenStuff {
        @SpirePostfixPatch
        public static void patch(CombatRewardScreen __instance) {
            for (RewardItem r : __instance.rewards) {
                if (r.cards != null) {
                    for (AbstractCard c : r.cards) {
                        rollCardAugment(c);
                    }
                }
            }
        }
    }

    @SpirePatch2(clz = AbstractDungeon.class, method = "getRewardCards")
    public static class ModifySpawnedCardsPatch {
        @SpirePostfixPatch
        public static void patch(ArrayList<AbstractCard> __result) {
            for (AbstractCard c : __result) {
                rollCardAugment(c, __result.indexOf(c));
            }
        }
    }

    @SpirePatch2(clz = AbstractDungeon.class, method = "getColorlessRewardCards")
    public static class ModifySpawnedColorlessCardsPatch {
        @SpirePostfixPatch
        public static void patch(ArrayList<AbstractCard> __result) {
            for (AbstractCard c : __result) {
                rollCardAugment(c, __result.indexOf(c));
            }
        }
    }

    @SpirePatch2(clz = GridCardSelectScreen.class, method = "openConfirmationGrid")
    public static class ModifyConfirmScreenCards {
        @SpirePostfixPatch
        public static void patch(CardGroup group) {
            if (CardAugmentsMod.modifyInstantObtain) {
                for (AbstractCard c : group.group) {
                    rollCardAugment(c);
                }
            }
        }
    }

    @SpirePatch2(clz = GremlinMatchGame.class, method = "initializeCards")
    public static class ModifyMatchGameCards {
        @SpireInsertPatch(locator = Locator.class, localvars = "retVal")
        public static void patch(ArrayList<AbstractCard> retVal) {
            for (AbstractCard c : retVal) {
                rollCardAugment(c);
            }
        }
        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractPlayer.class, "getStartCardForEvent");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }

    @SpirePatch2(clz = NeowReward.class, method = "getRewardCards")
    public static class ModifyNeowRewardCardsPatch {
        @SpirePostfixPatch
        public static void patch(ArrayList<AbstractCard> __result) {
            for (AbstractCard c : __result) {
                rollCardAugment(c);
            }
        }
    }

    @SpirePatch2(clz = NeowReward.class, method = "getColorlessRewardCards")
    public static class ModifyNeowRewardColorlessCardsPatch {
        @SpirePostfixPatch
        public static void patch(ArrayList<AbstractCard> __result) {
            for (AbstractCard c : __result) {
                rollCardAugment(c);
            }
        }
    }

    @SpirePatch2(clz = AbstractPlayer.class, method = "initializeStarterDeck")
    public static class ModifyStarterCards {
        @SpirePostfixPatch
        public static void patch(AbstractPlayer __instance) {
            if (CardAugmentsMod.modifyStarters) {
                for (AbstractCard c : __instance.masterDeck.group) {
                    rollCardAugment(c);
                }
            }
        }
    }

    @SpirePatch2(clz = PandorasBox.class, method = "onEquip")
    public static class ModifyPandoraCards {
        @SpireInsertPatch(locator = Locator.class, localvars = "group")
        public static void patch(CardGroup group) {
            if (CardAugmentsMod.modifyInstantObtain) {
                for (AbstractCard c : group.group) {
                    rollCardAugment(c);
                }
            }
        }
        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(GridCardSelectScreen.class, "openConfirmationGrid");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }

    @SpirePatch2(clz = TheLibrary.class, method = "buttonEffect")
    public static class ModifyTheLibraryCards {
        @SpireInsertPatch(locator = Locator.class, localvars = "group")
        public static void patch(CardGroup group) {
            for (AbstractCard c : group.group) {
                rollCardAugment(c);
            }
        }
        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(GridCardSelectScreen.class, "open");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }

    @SpirePatch2(clz = ShowCardAndObtainEffect.class, method = SpirePatch.CONSTRUCTOR, paramtypez = {AbstractCard.class, float.class, float.class, boolean.class})
    @SpirePatch2(clz = FastCardObtainEffect.class, method = SpirePatch.CONSTRUCTOR)
    public static class ModifySpawnedMasterDeckCards {
        @SpirePostfixPatch
        public static void patch(AbstractCard ___card) {
            if (CardAugmentsMod.modifyInstantObtain && !CopyTheDamnModPatches.needsCopy) {
                rollCardAugment(___card);
            }
        }
    }

    @SpirePatch2(clz = Merchant.class, method = SpirePatch.CONSTRUCTOR, paramtypez = {float.class, float.class, int.class})
    public static class ModifyShopCards {
        @SpireInsertPatch(locator = Locator.class)
        public static void patch(Merchant __instance, ArrayList<AbstractCard> ___cards1, ArrayList<AbstractCard> ___cards2) {
            if (CardAugmentsMod.modifyShop) {
                for (AbstractCard c : ___cards1) {
                    rollCardAugment(c);
                }
                for (AbstractCard c : ___cards2) {
                    rollCardAugment(c);
                }
            }
        }
        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(ShopScreen.class, "init");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }

    @SpirePatch2(clz = NeowEvent.class, method = "dailyBlessing")
    public static class RunModifierPatches {
        @SpireInsertPatch(locator = SealedLocator.class, localvars = "sealedGroup")
        public static void sealedDeck(CardGroup sealedGroup) {
            //Setting to rolled to prevent instant obtain is handled in StopTossingMods patch
            if (CardAugmentsMod.modifyStarters) {
                for (AbstractCard c : sealedGroup.group) {
                    rollCardAugment(c);
                }
            }
        }

        @SpireInsertPatch(locator = AddedLocator.class, localvars = "group")
        public static void addedCards(CardGroup group) {
            for (AbstractCard c : group.group) {
                if (modifyStarters) {
                    rollCardAugment(c);
                }
                //Don't let it try to roll on instant obtain, since these are starter cards
                RolledModFieldPatches.RolledModField.rolled.set(c, true);
            }
        }

        @SpireInsertPatch(locator = FastObtainLocator.class, localvars = "tmpCard")
        public static void fixSpecialized(AbstractCard tmpCard) {
            if (modifyStarters) {
                rollCardAugment(tmpCard);
            }
            //Don't let it try to roll on instant obtain, since these are starter cards
            RolledModFieldPatches.RolledModField.rolled.set(tmpCard, true);
        }

        public static class SealedLocator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                Matcher m = new Matcher.MethodCallMatcher(GridCardSelectScreen.class, "open");
                return LineFinder.findInOrder(ctBehavior, m);
            }
        }

        public static class AddedLocator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                Matcher m = new Matcher.MethodCallMatcher(GridCardSelectScreen.class, "openConfirmationGrid");
                return LineFinder.findInOrder(ctBehavior, m);
            }
        }

        public static class FastObtainLocator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                Matcher m = new Matcher.NewExprMatcher(FastCardObtainEffect.class);
                return LineFinder.findInOrder(ctBehavior, m);
            }
        }
    }

    @SpirePatch2(clz = CardRewardScreen.class, method = "draftOpen")
    public static class DraftFix {
        @SpirePostfixPatch
        public static void plz(CardRewardScreen __instance) {
            if (modifyStarters) {
                for (AbstractCard c : __instance.rewardGroup) {
                    rollCardAugment(c);
                }
            }
        }
    }
}
