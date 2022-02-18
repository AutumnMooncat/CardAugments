package CardAugments.damagemods;

import CardAugments.cardmods.rare.GreedMod;
import com.evacipated.cardcrawl.mod.stslib.damagemods.AbstractDamageModifier;
import com.evacipated.cardcrawl.mod.stslib.damagemods.DamageModifierManager;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.MinionPower;

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
