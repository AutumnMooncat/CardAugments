package CardAugments.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;

public class RolledModFieldPatches {
    @SpirePatch(clz = AbstractCard.class, method = "<class>")
    public static class RolledModField {
        public static SpireField<Boolean> rolled = new SpireField<>(() -> false);
    }

    @SpirePatch(clz = AbstractCard.class, method = "makeStatEquivalentCopy")
    public static class MakeStatEquivalentCopy {
        public static AbstractCard Postfix(AbstractCard result, AbstractCard self) {
            RolledModField.rolled.set(result, RolledModField.rolled.get(self));
            return result;
        }
    }
}
