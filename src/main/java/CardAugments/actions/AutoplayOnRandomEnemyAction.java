package CardAugments.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardQueueItem;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class AutoplayOnRandomEnemyAction extends AbstractGameAction {
    AbstractCard card;

    public AutoplayOnRandomEnemyAction(AbstractCard card) {
        this.card = card;
    }

    @Override
    public void update() {
        if (card != null && AbstractDungeon.actionManager.cardQueue.stream().noneMatch(i -> i.card == card)) {
            card.targetAngle = 0.0F;
            AbstractDungeon.actionManager.cardQueue.add(new CardQueueItem(this.card, AbstractDungeon.getRandomMonster())); //TODO null safety on cards that need a target for vfx but no alive monsters?
        }

        this.isDone = true;
    }

}
