package CardAugments.patches;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardQueueItem;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import javassist.CtBehavior;

public class EchoFieldPatches {

    @SpirePatch(clz = AbstractCard.class, method = "<class>")
    public static class EchoFields {
        public static SpireField<Integer> echo = new SpireField<>(() -> 0);
    }

    @SpirePatch(clz = AbstractCard.class, method = "makeStatEquivalentCopy")
    public static class MakeStatEquivalentCopy {
        public static AbstractCard Postfix(AbstractCard result, AbstractCard self) {
            EchoFields.echo.set(result, EchoFields.echo.get(self));
            return result;
        }
    }

    @SpirePatch(clz = AbstractPlayer.class, method = "useCard")
    public static class PlayExtraCopies {
        @SpireInsertPatch(locator = Locator.class)
        public static void withoutInfiniteLoopPls(AbstractPlayer __instance, AbstractCard c, AbstractMonster monster, int energyOnUse) {
//            if (M10RobotMod.isSpicyShopsLoaded) {
//                if (RepeatKeywordPatches.RepeatField.repeat.get(c)) {
//                    for (AbstractCardModifier mod : CardModifierManager.modifiers(c)) {
//                        if (mod instanceof AbstractExtraEffectModifier) {
//                            ((AbstractExtraEffectModifier) mod).doExtraEffects(c, __instance, monster);
//                        }
//                    }
//                }
//            }
            if (EchoFields.echo.get(c) > 0) {
                for (int i = 0; i < EchoFields.echo.get(c) ; i++) {
                    AbstractCard tmp = c.makeSameInstanceOf();
                    AbstractDungeon.player.limbo.addToBottom(tmp);
                    tmp.current_x = c.current_x;
                    tmp.current_y = c.current_y;
                    tmp.target_x = (float) Settings.WIDTH / 2.0F - 300.0F * Settings.scale;
                    tmp.target_y = (float) Settings.HEIGHT / 2.0F;
                    if (monster != null) {
                        tmp.calculateCardDamage(monster);
                    }
                    tmp.purgeOnUse = true;
                    //Don't loop infinitely, lol
                    EchoFields.echo.set(tmp, 0);
                    AbstractDungeon.actionManager.addCardQueueItem(new CardQueueItem(tmp, monster, c.energyOnUse, true, true), true);
                }
            }
        }
        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractCard.class, "use");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }
}
