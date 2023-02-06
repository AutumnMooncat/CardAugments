package CardAugments.util;

import Starlight.ui.ProjectedCardManager;
import com.megacrit.cardcrawl.cards.AbstractCard;

import java.util.ArrayList;

public class CrossoverHelper {
    public static class Sisters {
        public static ArrayList<AbstractCard> projectedCards() {
            return ProjectedCardManager.cards.group;
        }
    }
}
