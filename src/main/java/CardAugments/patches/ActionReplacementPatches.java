package CardAugments.patches;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import javassist.CtBehavior;

public class ActionReplacementPatches {
    public static AbstractCard cardInUse;
    public static AbstractMonster target;

    @SpirePatch(clz = AbstractCard.class, method = SpirePatch.CLASS)
    public static class InvertedFields {
        public static SpireField<Boolean> isInverted = new SpireField<>(() -> false);
        public static SpireField<Boolean> toBlock = new SpireField<>(() -> false);
    }

    @SpirePatch2(clz = GameActionManager.class, method = "addToTop")
    @SpirePatch2(clz = GameActionManager.class, method = "addToBottom")
    public static class ReplaceActions {
        @SpirePrefixPatch
        public static void plz(GameActionManager __instance, @ByRef AbstractGameAction[] action) {
            if (action[0].getClass().equals(DamageAction.class) || action[0].getClass().equals(GainBlockAction.class)) {
                if (cardInUse != null && InvertedFields.isInverted.get(cardInUse)) {
                    if (InvertedFields.toBlock.get(cardInUse)) {
                        action[0] = new GainBlockAction(AbstractDungeon.player, cardInUse.block);
                    } else {
                        if (target == null) {
                            target = AbstractDungeon.getRandomMonster();
                        }
                        action[0] = new DamageAction(target, new DamageInfo(AbstractDungeon.player, cardInUse.damage, cardInUse.damageTypeForTurn), AbstractGameAction.AttackEffect.FIRE);
                    }
                }
            }
        }
    }

    @SpirePatch(clz = AbstractPlayer.class, method = "useCard")
    public static class GrabCardInUse {
        @SpireInsertPatch(locator = Locator.class)
        public static void RememberCardPreUseCall(AbstractPlayer __instance, AbstractCard c, AbstractMonster monster, int energyOnUse) {
            cardInUse = c;
            target = monster;
        }

        @SpireInsertPatch(locator = Locator2.class)
        public static void ForgetCardPostUseCall(AbstractPlayer __instance, AbstractCard c, AbstractMonster monster, int energyOnUse) {
            cardInUse = null;
            target = null;
        }

        private static class Locator2 extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(GameActionManager.class, "addToBottom");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }

        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractCard.class, "use");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }
}
