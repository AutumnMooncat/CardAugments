package CardAugments.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpireInstrumentPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

public class ChangeRenderedNamePatches {
    private static String builtString;

    @SpirePatch(clz = AbstractCard.class, method = SpirePatch.CLASS)
    public static class PrefixSuffixFields {
        public static SpireField<String> prefix = new SpireField<>(() -> "");
        public static SpireField<String> suffix = new SpireField<>(() -> "");
    }

    public static String buildName(AbstractCard card, boolean isSCV) {
        builtString = PrefixSuffixFields.prefix.get(card) + card.name + PrefixSuffixFields.suffix.get(card);
        //TODO fix name scale if it gets too long
        return builtString;
    }

    @SpirePatch2(clz = AbstractCard.class, method = "renderTitle")
    public static class BeDifferentNamePls {
        @SpireInstrumentPatch
        public static ExprEditor patch() {
            return new ExprEditor() {
                @Override
                //Method call is basically the equivalent of a methodcallmatcher of an insert patch, checks the edit method against every method call in the function you#re patching
                public void edit(MethodCall m) throws CannotCompileException {
                    //If the method is from the class AnimationState and the method is called update
                    if (m.getClassName().equals(FontHelper.class.getName()) && m.getMethodName().equals("renderRotatedText")) {
                        m.replace("{" +
                                //"if(M10Robot.patches.BoosterFieldPatch.hasBoosterEquipped(this)) {" +
                                //$1 refers to the first input parameter of the method, in this case the float that Gdx.graphics.getDeltaTime() returns
                                "$3 = CardAugments.patches.ChangeRenderedNamePatches.buildName(this, false);" +
                                //"$10 = M10Robot.patches.ColorRenderingPatches.getCardNameColor(this, $10);" +
                                //"}" +
                                //Call the method as normal
                                "$proceed($$);" +
                                "}");
                    }
                }
            };
        }
    }

    @SpirePatch2(clz = SingleCardViewPopup.class, method = "renderTitle")
    public static class BeDifferentNamePls2 {
        @SpireInstrumentPatch
        public static ExprEditor patch() {
            return new ExprEditor() {
                @Override
                //Method call is basically the equivalent of a methodcallmatcher of an insert patch, checks the edit method against every method call in the function you#re patching
                public void edit(MethodCall m) throws CannotCompileException {
                    //If the method is from the class AnimationState and the method is called update
                    if (m.getClassName().equals(FontHelper.class.getName()) && m.getMethodName().equals("renderFontCentered")) {
                        m.replace("{" +
                                //"if(M10Robot.patches.BoosterFieldPatch.hasBoosterEquipped(this)) {" +
                                //$1 refers to the first input parameter of the method, in this case the float that Gdx.graphics.getDeltaTime() returns
                                "$3 = CardAugments.patches.ChangeRenderedNamePatches.buildName(this.card, true);" +
                                //"$10 = M10Robot.patches.ColorRenderingPatches.getCardNameColor(this, $10);" +
                                //"}" +
                                //Call the method as normal
                                "$proceed($$);" +
                                "}");
                    }
                }
            };
        }
    }
}
