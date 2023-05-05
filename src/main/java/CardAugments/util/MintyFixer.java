package CardAugments.util;

import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import mintySpire.patches.cards.betterUpdatePreview.CardFields;

import java.util.ArrayList;

public class MintyFixer {
    public static void fixMods(int index, AbstractCardModifier m) {
        ArrayList<AbstractCard> cards = CardFields.SCVPopup.unupgradedCardRewards.get(CardCrawlGame.cardPopup);
        if (cards != null && index < cards.size()) {
            CardModifierManager.addModifier(cards.get(index), m);
        }
    }
}
