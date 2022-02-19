package CardAugments.util;

import CardAugments.cards.DummyCard;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class CalcHelper {
    private static final DummyCard dummyCard = new DummyCard();

    public static int applyPowers(int damage) {
        dummyCard.setMultiDamage(false);
        dummyCard.baseDamage = damage;
        dummyCard.applyPowers();
        return dummyCard.damage;
    }

    public static int[] applyPowersMulti(int damage) {
        dummyCard.setMultiDamage(true);
        dummyCard.baseDamage = damage;
        dummyCard.applyPowers();
        return dummyCard.multiDamage;
    }

    public static int applyPowersToBlock(int block) {
        dummyCard.baseBlock = block;
        dummyCard.applyPowers();
        return dummyCard.block;
    }

    public static int calculateCardDamage(int damage, AbstractMonster mo) {
        dummyCard.setMultiDamage(false);
        dummyCard.baseDamage = damage;
        dummyCard.calculateCardDamage(mo);
        return dummyCard.damage;
    }

    public static int[] calculateCardDamageMulti(int damage) {
        dummyCard.setMultiDamage(true);
        dummyCard.baseDamage = damage;
        dummyCard.calculateCardDamage(null);
        return dummyCard.multiDamage;
    }

}
