package CardAugments.util.crossover;

import Starlight.ui.ProjectedCardManager;
import com.megacrit.cardcrawl.cards.AbstractCard;

import java.util.ArrayList;

public class SistersHelper {
    public static ArrayList<AbstractCard> projectedCards() {
        ArrayList<AbstractCard> cards = new ArrayList<>(ProjectedCardManager.cards.group);
        cards.addAll(ProjectedCardManager.renderQueue.group);
        return cards;
    }
}
