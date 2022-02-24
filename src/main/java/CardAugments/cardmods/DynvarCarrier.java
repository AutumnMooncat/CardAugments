package CardAugments.cardmods;

import com.megacrit.cardcrawl.cards.AbstractCard;

public interface DynvarCarrier {
    String key();
    int val(AbstractCard card);
    int baseVal(AbstractCard card);
    boolean modified(AbstractCard card);
    boolean upgraded(AbstractCard card);
}
