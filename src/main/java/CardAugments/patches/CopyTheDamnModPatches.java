package CardAugments.patches;

import CardAugments.cardmods.AbstractAugment;
import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.events.shrines.GremlinMatchGame;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import javassist.CtBehavior;

import java.util.ArrayList;

public class CopyTheDamnModPatches {
    private static final ArrayList<AbstractCardModifier> modsToCopy = new ArrayList<>();

    @SpirePatch2(clz = GremlinMatchGame.class, method = "updateMatchGameLogic")
    public static class ModifyMatchGameCards {
        @SpireInsertPatch(locator = Locator.class)
        public static void patch(AbstractCard ___chosenCard) {
            for (AbstractCardModifier m : CardModifierManager.modifiers(___chosenCard)) {
                if (m instanceof AbstractAugment) {
                    modsToCopy.add(m);
                }
            }
        }
        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(ArrayList.class, "add");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }

    @SpirePatch2(clz = ShowCardAndObtainEffect.class, method = SpirePatch.CONSTRUCTOR, paramtypez = {AbstractCard.class, float.class, float.class, boolean.class})
    public static class CopyTheMod {
        public static void Postfix(AbstractCard card) {
            for (AbstractCardModifier m : modsToCopy) {
                CardModifierManager.addModifier(card, m.makeCopy());
            }
            modsToCopy.clear();
        }
    }
}
