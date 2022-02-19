package CardAugments.damagemods;

import com.evacipated.cardcrawl.mod.stslib.damagemods.AbstractDamageModifier;
import com.evacipated.cardcrawl.mod.stslib.damagemods.DamageModifierManager;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.MinionPower;
import com.megacrit.cardcrawl.vfx.GainPennyEffect;

public class GreedDamage extends AbstractDamageModifier {
    int gold;

    public GreedDamage(int gold) {
        this.priority = Short.MAX_VALUE;
        this.gold = gold;
    }

    @Override
    public void onLastDamageTakenUpdate(DamageInfo info, int lastDamageTaken, int overkillAmount, AbstractCreature targetHit) {
        if (DamageModifierManager.getInstigator(info) instanceof AbstractCard) {
            if (targetHit.currentHealth > 0 && targetHit.currentHealth - lastDamageTaken <= 0 && !targetHit.halfDead && !targetHit.hasPower(MinionPower.POWER_ID)) {
                AbstractDungeon.player.gainGold(gold);
                for(int i = 0; i < this.gold; ++i) {
                    AbstractDungeon.effectList.add(new GainPennyEffect(info.owner,targetHit.hb.cX, targetHit.hb.cY, info.owner.hb.cX, info.owner.hb.cY, true));
                }
            }
        }
    }

    @Override
    public boolean isInherent() {
        return true;
    }

    @Override
    public AbstractDamageModifier makeCopy() {
        return new GreedDamage(gold);
    }
}
