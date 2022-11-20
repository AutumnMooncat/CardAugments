package CardAugments.patches;

import basemod.abstracts.CustomMonster;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;
import javassist.expr.NewExpr;

import java.util.ArrayList;

public class RunicPatch {
    @SpirePatch(
            clz = AbstractMonster.class,
            method = "renderTip"
    )
    public static class TipPatch {
        @SpireInsertPatch (locator = Locator.class)
        public static void Insert(AbstractMonster __instance, SpriteBatch sb, ArrayList<PowerTip> ___tips, PowerTip ___intentTip) {
            if (AbstractDungeon.player.hasPower(CardAugments.powers.RunicPower.POWER_ID))
                ___tips.remove(___intentTip);
        }
        public static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                return LineFinder.findInOrder(ctBehavior, new Matcher.MethodCallMatcher(ArrayList.class, "isEmpty"));
            }
        }
    }

    @SpirePatch(clz = AbstractMonster.class, method = "render")
    @SpirePatch(clz = CustomMonster.class, method = "render")
    public static class IntentPatch {
        @SpireInstrumentPatch
        public static ExprEditor Instrument() {
            return new ExprEditor() {
                @Override
                public void edit(MethodCall m) throws CannotCompileException {
                    if (m.getMethodName().equals("hasRelic")) {
                        m.replace("{$_ = $proceed($$) || com.megacrit.cardcrawl.dungeons.AbstractDungeon.player.hasPower(CardAugments.powers.RunicPower.POWER_ID);}");
                    }
                }
            };
        }
    }
}
