package CardAugments.patches;

import CardAugments.cardmods.AbstractAugment;
import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;

public class CheesyOnUpgradePatch {
    @SpirePatch2(clz = AbstractPlayer.class, method = "bottledCardUpgradeCheck")
    public static class SureHopeNobodyMakesAnUpgradeMechanicThatDoesntCallThis {
        @SpirePostfixPatch
        public static void onUpgrade(AbstractCard c) {
            for (AbstractCardModifier m : CardModifierManager.modifiers(c)) {
                if (m instanceof AbstractAugment) {
                    ((AbstractAugment) m).onUpgradeCheck(c);
                }
            }
        }
    }

    @SpirePatch2(clz = AbstractCard.class, method = "displayUpgrades")
    public static class ThisOneIsMoreReasonable {
        @SpirePostfixPatch
        public static void onPreview(AbstractCard __instance) {
            for (AbstractCardModifier m : CardModifierManager.modifiers(__instance)) {
                if (m instanceof AbstractAugment) {
                    ((AbstractAugment) m).onUpgradeCheck(__instance);
                }
            }
        }
    }
}
