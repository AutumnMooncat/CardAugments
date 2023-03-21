package CardAugments.patches;

import basemod.helpers.CardModifierManager;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.curses.Necronomicurse;
import com.megacrit.cardcrawl.vfx.NecronomicurseEffect;

public class CopyNecroModsPatches {
    static AbstractCard backupCard = null;

    @SpirePatch2(clz = Necronomicurse.class, method = "onRemoveFromMasterDeck")
    @SpirePatch2(clz = Necronomicurse.class, method = "triggerOnExhaust")
    public static class CopyMods {
        @SpirePrefixPatch
        public static void plz(AbstractCard __instance) {
            backupCard = __instance;
        }
    }

    @SpirePatch2(clz = MakeTempCardInHandAction.class, method = SpirePatch.CONSTRUCTOR, paramtypez = {AbstractCard.class, int.class})
    public static class ApplyMods {
        @SpirePostfixPatch
        public static void plz(AbstractCard card) {
            if (backupCard != null) {
                CardModifierManager.copyModifiers(backupCard, card, true, false, false);
                backupCard = null;
            }
        }
    }

    @SpirePatch2(clz = NecronomicurseEffect.class, method = SpirePatch.CONSTRUCTOR)
    public static class ApplyMods2 {
        @SpirePostfixPatch
        public static void plz(AbstractCard card) {
            if (backupCard != null) {
                CardModifierManager.copyModifiers(backupCard, card, true, false, false);
                backupCard = null;
            }
        }
    }
}
