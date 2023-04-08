package CardAugments.patches;

import CardAugments.cardmods.common.MassiveMod;
import CardAugments.cardmods.common.TinyMod;
import CardAugments.cardmods.uncommon.BlurryMod;
import SpireLocations.patches.nodemodifierhooks.ModifyRewardsPatch;
import basemod.helpers.CardModifierManager;
import blurryblur.CardPatches;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireInstrumentPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.green.Blur;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.screens.CombatRewardScreen;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;
import rainbowMod.patches.RainbowCardsInChestsPatch;

import static CardAugments.CardAugmentsMod.rollCardAugment;

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

    @SpirePatch2(clz = AbstractCard.class, method = "render", paramtypez = {SpriteBatch.class}, requiredModId = "bigcards", optional = true)
    public static class SizeChanges {
        @SpirePrefixPatch
        public static void changeSize(AbstractCard __instance) {
            if (CardModifierManager.hasModifier(__instance, MassiveMod.ID)) {
                __instance.drawScale *= 2/1.5f;
            }
            if (CardModifierManager.hasModifier(__instance, TinyMod.ID)) {
                __instance.drawScale *= 1.5/2f;
            }
        }
        @SpirePostfixPatch
        public static void resetSize(AbstractCard __instance) {
            if (CardModifierManager.hasModifier(__instance, MassiveMod.ID)) {
                __instance.drawScale /= 2/1.5f;
            }
            if (CardModifierManager.hasModifier(__instance, TinyMod.ID)) {
                __instance.drawScale /= 1.5/2f;
            }
        }
    }

    @SpirePatch2(clz = AbstractCard.class, method = "renderHoverShadow", requiredModId = "bigcards", optional = true)
    public static class FixShadow {
        @SpireInstrumentPatch
        public static ExprEditor plz() {
            return new ExprEditor() {
                @Override
                public void edit(MethodCall m) throws CannotCompileException {
                    if (m.getClassName().equals(AbstractCard.class.getName()) && m.getMethodName().equals("renderHelper")) {
                        m.replace("$6 = "+CompatibilityPatches.class.getName()+".getScale($0, $6); $_ = $proceed($$);");
                    }
                }
            };
        }
    }

    public static float getScale(AbstractCard card, float scale) {
        if (CardModifierManager.hasModifier(card, MassiveMod.ID)) {
            scale *= 2/1.5f;
        }
        if (CardModifierManager.hasModifier(card, TinyMod.ID)) {
            scale *= 1.5/2f;
        }
        return scale;
    }

    @SpirePatch2(clz = CardPatches.NormalRender.class, method = "InsertBefore", requiredModId = "blurryblur", optional = true)
    public static class BlurOn {
        @SpirePostfixPatch
        public static void plz(Object[] __args) {
            if (__args[1] instanceof SpriteBatch && __args[0] instanceof AbstractCard) {
                if (CardModifierManager.hasModifier((AbstractCard) __args[0], BlurryMod.ID)) {
                    if (__args[0] instanceof Blur) {
                        CardPatches.INSTANCE.begin((Batch) __args[1], 4.0f);
                    } else {
                        CardPatches.INSTANCE.begin((Batch) __args[1], 2.0f);
                    }
                }
            }
        }
    }

    @SpirePatch2(clz = CardPatches.NormalRender.class, method = "InsertAfter", requiredModId = "blurryblur", optional = true)
    public static class BlurOff {
        @SpirePostfixPatch
        public static void plz(Object[] __args) {
            if (__args[1] instanceof SpriteBatch && __args[0] instanceof AbstractCard) {
                if (CardModifierManager.hasModifier((AbstractCard) __args[0], BlurryMod.ID)) {
                    CardPatches.INSTANCE.end((Batch) __args[1]);
                }
            }
        }
    }

    @SpirePatch2(clz = CardPatches.SCVRender.class, method = "InsertBefore", requiredModId = "blurryblur", optional = true)
    public static class BlurOnSCV {
        @SpirePostfixPatch
        public static void plz(Object[] __args) {
            if (__args[0] instanceof SpriteBatch && __args[1] instanceof AbstractCard) {
                if (CardModifierManager.hasModifier((AbstractCard) __args[1], BlurryMod.ID)) {
                    if (__args[1] instanceof Blur) {
                        CardPatches.INSTANCE.begin((Batch) __args[0], 8.0f);
                    } else {
                        CardPatches.INSTANCE.begin((Batch) __args[0], 4.0f);
                    }
                }
            }
        }
    }

    @SpirePatch2(clz = CardPatches.SCVRender.class, method = "InsertAfter", requiredModId = "blurryblur", optional = true)
    public static class BlurOffSCV {
        @SpirePostfixPatch
        public static void plz(Object[] __args) {
            if (__args[0] instanceof SpriteBatch && __args[1] instanceof AbstractCard) {
                if (CardModifierManager.hasModifier((AbstractCard) __args[1], BlurryMod.ID)) {
                    CardPatches.INSTANCE.end((Batch) __args[0]);
                }
            }
        }
    }

    @SpirePatch2(clz = RainbowCardsInChestsPatch.class, method = "makeRainbowReward", requiredModId = "RainbowMod", optional = true)
    public static class ModsOnRainbowCards {
        @SpirePostfixPatch
        public static void modTime(RewardItem __result) {
            if (__result.cards != null) {
                for (AbstractCard c : __result.cards) {
                    rollCardAugment(c);
                }
            }
        }
    }
}
