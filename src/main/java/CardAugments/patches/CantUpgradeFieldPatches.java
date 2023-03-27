package CardAugments.patches;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;

public class CantUpgradeFieldPatches {
    @SpirePatch(clz = AbstractCard.class, method = "<class>")
    public static class CantUpgradeField {
        public static SpireField<Boolean> preventUpgrades = new SpireField<>(() -> false);
    }

    public static boolean cantUpgradeCheck(AbstractCard card) {
        return CantUpgradeField.preventUpgrades.get(card);
    }

    @SpirePatch(clz = AbstractCard.class, method = "makeStatEquivalentCopy")
    public static class MakeStatEquivalentCopy {
        public static AbstractCard Postfix(AbstractCard result, AbstractCard self) {
            CantUpgradeField.preventUpgrades.set(result, CantUpgradeField.preventUpgrades.get(self));
            return result;
        }
    }

    @SpirePatch2(clz = AbstractCard.class, method = "canUpgrade")
    public static class BypassUpgradeLimit {
        @SpirePrefixPatch
        public static SpireReturn<?> plz(AbstractCard __instance) {
            if (CantUpgradeField.preventUpgrades.get(__instance)) {
                return SpireReturn.Return(false);
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch2(clz = SingleCardViewPopup.class, method = "allowUpgradePreview")
    public static class RemoveUpgradeButton {
        @SpirePrefixPatch
        public static SpireReturn<?> plz(SingleCardViewPopup __instance, AbstractCard ___card) {
            if (CantUpgradeField.preventUpgrades.get(___card)) {
                return SpireReturn.Return(false);
            }
            return SpireReturn.Continue();
        }
    }
}
