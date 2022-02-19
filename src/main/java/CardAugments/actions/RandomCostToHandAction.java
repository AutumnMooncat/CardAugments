package CardAugments.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.utility.DiscardToHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import java.util.Iterator;

public class RandomCostToHandAction extends AbstractGameAction {
    private AbstractPlayer p;
    private int costTarget;

    public RandomCostToHandAction(int costToTarget) {
        this.p = AbstractDungeon.player;
        this.setValues(this.p, AbstractDungeon.player, this.amount);
        this.actionType = ActionType.CARD_MANIPULATION;
        this.costTarget = costToTarget;
    }

    public void update() {
        CardGroup tmp = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
        for (AbstractCard c : p.discardPile.group) {
            if (c.cost == costTarget || c.freeToPlayOnce) {
                tmp.addToTop(c);
            }
        }
        if (!tmp.isEmpty()) {
            this.addToTop(new DiscardToHandAction(tmp.getRandomCard(true)));
            tmp.clear();
        }
        this.isDone = true;
    }
}
