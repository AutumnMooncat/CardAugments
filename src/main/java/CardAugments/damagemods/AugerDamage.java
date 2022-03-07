package CardAugments.damagemods;

import com.evacipated.cardcrawl.mod.stslib.damagemods.AbstractDamageModifier;
import com.evacipated.cardcrawl.mod.stslib.damagemods.DamageModifierManager;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.VulnerablePower;

public class AugerDamage extends AbstractDamageModifier {
    int vuln;

    public AugerDamage(int vuln) {
        this.vuln = vuln;
    }

    @Override
    public void onDamageModifiedByBlock(DamageInfo info, int unblockedAmount, int blockedAmount, AbstractCreature target) {
        if (DamageModifierManager.getDamageMods(info).stream().noneMatch(m -> m.ignoresBlock(target))) {
            if (target.currentBlock > 0 && (unblockedAmount > 0 || blockedAmount == target.currentBlock)) {
                this.addToBot(new ApplyPowerAction(target, info.owner, new VulnerablePower(target, vuln, false)));
            }
        }
    }

    @Override
    public boolean isInherent() {
        return true;
    }

    @Override
    public AbstractDamageModifier makeCopy() {
        return new AugerDamage(vuln);
    }
}
