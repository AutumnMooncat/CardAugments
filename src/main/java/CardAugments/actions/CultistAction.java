package CardAugments.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.relics.CultistMask;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;
import com.megacrit.cardcrawl.vfx.combat.InflameEffect;

public class CultistAction extends AbstractGameAction {

    public CultistAction() {}

    public void update() {
        if (AbstractDungeon.actionManager.cardsPlayedThisCombat.size() >= 2) {
            int amount = AbstractDungeon.actionManager.cardsPlayedThisCombat.get(AbstractDungeon.actionManager.cardsPlayedThisCombat.size() - 2).cost;
            if (amount == -1) {
                amount = AbstractDungeon.player.energy.energyMaster;
            }
            this.addToTop(new TalkAction(true, CardCrawlGame.languagePack.getRelicStrings(CultistMask.ID).DESCRIPTIONS[1], 0.0F, 2.0F));
            if (amount > 0) {
                this.addToTop(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new StrengthPower(AbstractDungeon.player, amount), amount, true));
            }
            this.addToTop(new VFXAction(AbstractDungeon.player, new InflameEffect(AbstractDungeon.player), 0.0F));
            this.addToTop(new SFXAction("VO_CULTIST_1A"));
        }
        this.isDone = true;
    }
}
