package CardAugments.damagemods;

import CardAugments.cardmods.util.FlatBaseDamageMod;
import basemod.helpers.CardModifierManager;
import com.evacipated.cardcrawl.mod.stslib.damagemods.AbstractDamageModifier;
import com.evacipated.cardcrawl.mod.stslib.damagemods.DamageModifierManager;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.GetAllInBattleInstances;
import com.megacrit.cardcrawl.powers.MinionPower;

public class MoxieDamage extends AbstractDamageModifier {
    int boost;

    public MoxieDamage(int boost) {
        this.priority = Short.MAX_VALUE;
        this.boost = boost;
    }

    @Override
    public void onLastDamageTakenUpdate(DamageInfo info, int lastDamageTaken, int overkillAmount, AbstractCreature targetHit) {
        if (DamageModifierManager.getInstigator(info) instanceof AbstractCard) {
            AbstractCard c = (AbstractCard) DamageModifierManager.getInstigator(info);
            if (targetHit.currentHealth > 0 && targetHit.currentHealth - lastDamageTaken <= 0 && !targetHit.halfDead && !targetHit.hasPower(MinionPower.POWER_ID)) {
                for (AbstractCard card : AbstractDungeon.player.masterDeck.group) {
                    if (card.uuid.equals(c.uuid)) {
                        CardModifierManager.addModifier(card, new FlatBaseDamageMod(boost));
                    }
                }

                for (AbstractCard card : GetAllInBattleInstances.get(c.uuid)) {
                    CardModifierManager.addModifier(card, new FlatBaseDamageMod(boost));
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
        return new MoxieDamage(boost);
    }
}
