package CardAugments.patches;

import SpireLocations.patches.nodemodifierhooks.ModifyRewardsPatch;
import basemod.helpers.CardModifierManager;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.screens.CombatRewardScreen;
import visiblecardrewards.patches.NewRewardtypePatch;
import visiblecardrewards.rewards.SingleCardReward;

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

    @SpirePatch2(clz = SingleCardReward.class, method = "init", requiredModId = "visiblecardrewards", optional = true)
    public static class UseModifiedNames {
        @SpirePostfixPatch
        public static void plz(SingleCardReward __instance) {
            if (__instance.type == NewRewardtypePatch.VCR_SINGLECARDREWARD) {
                __instance.text = CardModifierManager.onRenderTitle(__instance.card, __instance.card.name);
            }
        }
    }
}
