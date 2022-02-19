package CardAugments.patches;

import CardAugments.cardmods.AbstractAugment;
import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

public class CardModExtraHooksPatches {
    @SpirePatch2(clz = AbstractPlayer.class, method = "updateCardsOnDamage")
    public static class UpdateOnDamage {
        @SpirePostfixPatch
        public static void update(AbstractPlayer __instance) {
            if (AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT) {
                for (AbstractCard c : __instance.hand.group) {
                    update(c);
                }

                for (AbstractCard c : __instance.drawPile.group) {
                    update(c);
                }

                for (AbstractCard c : __instance.discardPile.group) {
                    update(c);
                }
            }
        }

        public static void update(AbstractCard c) {
            for (AbstractCardModifier m : CardModifierManager.modifiers(c)) {
                if (m instanceof AbstractAugment) {
                    ((AbstractAugment) m).onDamaged(c);
                }
            }
        }
    }
}
