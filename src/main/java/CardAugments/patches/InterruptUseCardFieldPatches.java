package CardAugments.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpireInstrumentPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

public class InterruptUseCardFieldPatches {
    @SpirePatch(clz = AbstractCard.class, method = "<class>")
    public static class InterceptUseField {
        public static SpireField<Boolean> interceptUse = new SpireField<>(() -> false);
    }

    @SpirePatch(clz = AbstractCard.class, method = "makeStatEquivalentCopy")
    public static class MakeStatEquivalentCopy {
        public static AbstractCard Postfix(AbstractCard result, AbstractCard self) {
            InterceptUseField.interceptUse.set(result, InterceptUseField.interceptUse.get(self));
            return result;
        }
    }

    public static boolean interceptCheck(AbstractCard card) {
        return !InterceptUseField.interceptUse.get(card);
    }

    @SpirePatch2(clz = AbstractPlayer.class, method = "useCard")
    public static class InterceptPls {
        @SpireInstrumentPatch
        public static ExprEditor patch() {
            return new ExprEditor() {
                @Override
                //Method call is basically the equivalent of a methodcallmatcher of an insert patch, checks the edit method against every method call in the function you#re patching
                public void edit(MethodCall m) throws CannotCompileException {
                    //If the method is from the class AnimationState and the method is called update
                    if (m.getClassName().equals(AbstractCard.class.getName()) && m.getMethodName().equals("use")) {
                        m.replace("if(CardAugments.patches.InterruptUseCardFieldPatches.interceptCheck($0)) {$proceed($$);}");
                    }
                }
            };
        }
    }
}
