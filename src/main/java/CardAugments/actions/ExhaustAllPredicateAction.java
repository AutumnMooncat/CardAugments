package CardAugments.actions;

import com.badlogic.gdx.utils.Predicate;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ExhaustSpecificCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class ExhaustAllPredicateAction extends AbstractGameAction {
    Predicate<AbstractCard> pred;

    public ExhaustAllPredicateAction(Predicate<AbstractCard> pred) {
        this.pred = pred;
        this.actionType = ActionType.WAIT;
        this.duration = this.startDuration = Settings.ACTION_DUR_FAST;
    }

    public void update() {
        if (duration == startDuration) {
            for (AbstractCard c : AbstractDungeon.player.hand.group) {
                if (pred.evaluate(c)) {
                    this.addToTop(new ExhaustSpecificCardAction(c, AbstractDungeon.player.hand));
                }
            }
            this.isDone = true;
        }

    }
}
