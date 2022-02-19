package CardAugments.patches;

import CardAugments.cardmods.AbstractAugment;
import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;

public class UpdateDynvarValuesPatches {
    @SpirePatch2(clz = AbstractPlayer.class, method = "bottledCardUpgradeCheck")
    public static class FixDynVarModsOnUpgrade {
        @SpirePostfixPatch
        public static void plz(AbstractCard c) {
            for (AbstractCardModifier m : CardModifierManager.modifiers(c)) {
                if (m instanceof AbstractAugment) {
                    ((AbstractAugment) m).updateDynvar(c);
                }
            }
        }
    }

    @SpirePatch2(clz = AbstractCard.class, method = "resetAttributes")
    public static class FixDynVarModsWhenResettingVars {
        @SpirePostfixPatch
        public static void plz(AbstractCard __instance) {
            for (AbstractCardModifier m : CardModifierManager.modifiers(__instance)) {
                if (m instanceof AbstractAugment) {
                    ((AbstractAugment) m).updateDynvar(__instance);
                }
            }
        }
    }
}
