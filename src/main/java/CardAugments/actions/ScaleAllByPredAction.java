package CardAugments.actions;

import CardAugments.util.CrossoverHelper;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import java.util.ArrayList;
import java.util.function.Predicate;

public class ScaleAllByPredAction extends AbstractGameAction {
    public enum StatBoost {
        DAMAGE,
        BLOCK,
        MAGIC
    }

    private final AbstractCard card;
    private final Predicate<AbstractCard> pred;
    private final StatBoost stat;

    public ScaleAllByPredAction(AbstractCard card, int amount, StatBoost stat, Predicate<AbstractCard> pred) {
        this.card = card;
        this.amount = amount;
        this.pred = pred;
        this.stat = stat;
    }

    @Override
    public void update() {
        ArrayList<AbstractCard> cards = new ArrayList<>();
        cards.add(card);
        cards.addAll(AbstractDungeon.player.drawPile.group);
        cards.addAll(AbstractDungeon.player.hand.group);
        cards.addAll(AbstractDungeon.player.discardPile.group);
        if (Loader.isModLoaded("Starlight")) {
            cards.addAll(CrossoverHelper.Sisters.projectedCards());
        }

        for (AbstractCard c : cards) {
            if (pred.test(c)) {
                switch (stat) {
                    case DAMAGE:
                        c.baseDamage += amount;
                        break;
                    case BLOCK:
                        c.baseBlock += amount;
                        break;
                    case MAGIC:
                        c.baseMagicNumber += amount;
                        c.magicNumber += amount;
                        break;
                }
                c.applyPowers();
            }
        }

        this.isDone = true;
    }
}
