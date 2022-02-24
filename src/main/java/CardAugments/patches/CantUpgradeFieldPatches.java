package CardAugments.patches;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;

public class CantUpgradeFieldPatches {
    @SpirePatch(clz = AbstractCard.class, method = "<class>")
    public static class CantUpgradeField {
        public static SpireField<Boolean> preventUpgrades = new SpireField<>(() -> false);
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
}
