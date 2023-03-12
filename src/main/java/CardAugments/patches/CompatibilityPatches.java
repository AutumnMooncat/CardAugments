package CardAugments.patches;

import SpireLocations.patches.nodemodifierhooks.ModifyRewardsPatch;
import basemod.helpers.CardModifierManager;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.screens.CombatRewardScreen;

public class CompatibilityPatches {
    @SpirePatch2(clz = ModifyRewardsPatch.class, method = "addModifiersRewards", requiredModId = "spirelocations", optional = true)
    public static class HitSpireLocationsRewards {
        @SpirePostfixPatch
        public static void plz(Object[] __args) {
            if (__args[0] instanceof CombatRewardScreen) {
                OnCardGeneratedPatches.ModifyRewardScreenStuff.patch((CombatRewardScreen) __args[0]);
            }
        }
    }

    @SpirePatch2(clz = visiblecardrewards.rewards.SingleCardReward.class, method = "init", requiredModId = "visiblecardrewards", optional = true)
    public static class UseModifiedNames {
        @SpirePostfixPatch
        public static void plz(visiblecardrewards.rewards.SingleCardReward __instance) {
            if (__instance.type == visiblecardrewards.patches.NewRewardtypePatch.VCR_SINGLECARDREWARD) {
                __instance.text = CardModifierManager.onRenderTitle(__instance.card, __instance.card.name);
            }
        }
    }

    @SpirePatch2(clz = oceanmod.rewards.SingleCardReward.class, method = "init", requiredModId = "oceanmod", optional = true)
    public static class UseModifiedNames2 {
        @SpirePostfixPatch
        public static void plz(oceanmod.rewards.SingleCardReward __instance) {
            if (__instance.type == oceanmod.patches.visiblecardrewards.NewRewardtypePatch.VCR_SINGLECARDREWARD) {
                __instance.text = CardModifierManager.onRenderTitle(__instance.card, __instance.card.name);
            }
        }
    }
}
