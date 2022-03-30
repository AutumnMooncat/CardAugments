package CardAugments.patches;

import CardAugments.cardmods.AbstractAugment;
import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class CanPlayPatches {
    @SpirePatch(clz = AbstractCard.class, method = "hasEnoughEnergy")
    public static class CardModifierCanPlayCard {
        public static SpireReturn<Boolean> Prefix(AbstractCard __instance) {
            for (AbstractCard c : AbstractDungeon.player.hand.group) {
                for (AbstractCardModifier m : CardModifierManager.modifiers(c)) {
                    if (m instanceof AbstractAugment) {
                        if (!((AbstractAugment) m).betterCanPlay(c, __instance)) {
                            return SpireReturn.Return(false);
                        }
                    }
                }
            }
            return SpireReturn.Continue();
        }
    }
}
