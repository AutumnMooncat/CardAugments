package CardAugments.actions;

import CardAugments.util.CalcHelper;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.combat.AnimatedSlashEffect;

public class AndTearAction extends AbstractGameAction {
    private Color color = Color.RED;
    private Color color2 = Color.GOLD;

    public AndTearAction(int baseDamage) {
        this.amount = baseDamage;
        this.source = AbstractDungeon.player;
        this.actionType = ActionType.DAMAGE;
    }

    @Override
    public void update() {
        AbstractMonster m = AbstractDungeon.getRandomMonster();
        if (m != null) {
            if (MathUtils.randomBoolean()) {
                CardCrawlGame.sound.playA("ATTACK_DAGGER_5", MathUtils.random(0.0F, -0.3F));
            } else {
                CardCrawlGame.sound.playA("ATTACK_DAGGER_6", MathUtils.random(0.0F, -0.3F));
            }

            float baseAngle;

            if (MathUtils.randomBoolean()) {
                baseAngle = 135.0F;
                AbstractDungeon.effectsQueue.add(new AnimatedSlashEffect(m.hb.cX - 45.0F, m.hb.cY + 45.0F, -150.0F, -150.0F, baseAngle + MathUtils.random(-10.0F, 10.0F), this.color, this.color2));
                AbstractDungeon.effectsQueue.add(new AnimatedSlashEffect(m.hb.cX, m.hb.cY, -150.0F, -150.0F, baseAngle + MathUtils.random(-10.0F, 10.0F), this.color, this.color2));
                AbstractDungeon.effectsQueue.add(new AnimatedSlashEffect(m.hb.cX + 45.0F, m.hb.cY - 45.0F, -150.0F, -150.0F, baseAngle + MathUtils.random(-10.0F, 10.0F), this.color, this.color2));
            } else {
                baseAngle = -135.0F;
                AbstractDungeon.effectsQueue.add(new AnimatedSlashEffect(m.hb.cX - 45.0F, m.hb.cY - 45.0F, 150.0F, -150.0F, baseAngle + MathUtils.random(-10.0F, 10.0F), this.color, this.color2));
                AbstractDungeon.effectsQueue.add(new AnimatedSlashEffect(m.hb.cX, m.hb.cY, 150.0F, -150.0F, baseAngle + MathUtils.random(-10.0F, 10.0F), this.color, this.color2));
                AbstractDungeon.effectsQueue.add(new AnimatedSlashEffect(m.hb.cX + 40.0F, m.hb.cY + 40.0F, 150.0F, -150.0F, baseAngle + MathUtils.random(-10.0F, 10.0F), this.color, this.color2));
            }

            addToTop(new DamageAction(m, new DamageInfo(AbstractDungeon.player, CalcHelper.calculateCardDamage(amount, m), DamageInfo.DamageType.NORMAL), AbstractGameAction.AttackEffect.NONE));
        }
        this.isDone = true;
    }
}
