package CardAugments.patches;

import CardAugments.cardmods.AbstractAugment;
import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import com.megacrit.cardcrawl.cards.AbstractCard;

public class OnUpgradePatch {
    public static void onUpgrade(AbstractCard card) {
        for (AbstractCardModifier m : CardModifierManager.modifiers(card)) {
            if (m instanceof AbstractAugment) {
                ((AbstractAugment) m).onUpgradeCheck(card);
            }
        }
    }
}
