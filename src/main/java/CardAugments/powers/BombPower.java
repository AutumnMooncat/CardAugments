package CardAugments.powers;

import CardAugments.CardAugmentsMod;
import CardAugments.util.FormatHelper;
import basemod.ReflectionHacks;
import com.badlogic.gdx.Gdx;
import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.NonStackablePower;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardQueueItem;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.combat.SilentGainPowerEffect;

import java.util.ArrayList;

public class BombPower extends AbstractPower implements NonStackablePower {
    public static final String POWER_ID = CardAugmentsMod.makeID(BombPower.class.getSimpleName());
    private static final PowerStrings TEXT = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    private final AbstractCard card;
    private final int cards;
    private float flashTimer = -1f;
    private final ArrayList<AbstractGameEffect> array;

    public BombPower(AbstractCreature owner, int amount, int cards, AbstractCard card) {
        name = TEXT.NAME + card.name;
        ID = POWER_ID;
        this.amount = amount;
        this.cards = cards;
        this.owner = owner;
        this.card = card.makeStatEquivalentCopy();
        type = PowerType.BUFF;
        isTurnBased = false;
        this.loadRegion("the_bomb");
        updateDescription();
        this.array = ReflectionHacks.getPrivateInherited(this, BombPower.class, "effect");

    }

    public void update(int slot) {
        super.update(slot);
        if (flashTimer != -1f) {
            this.flashTimer += Gdx.graphics.getDeltaTime();
            if (this.flashTimer > 1.0F) {
                this.array.add(new SilentGainPowerEffect(this));
                this.flashTimer = 0.0F;
            }
        }
    }

    @Override
    public void updateDescription() {
        if (amount == 1) {
            description = String.format(TEXT.DESCRIPTIONS[1], cards, FormatHelper.prefixWords(card.name, "#y"));
        } else {
            description = String.format(TEXT.DESCRIPTIONS[0], amount, cards, FormatHelper.prefixWords(card.name, "#y"));
        }

    }

    @Override
    public void atStartOfTurnPostDraw() {
        if (!AbstractDungeon.getMonsters().areMonstersBasicallyDead()) {
            this.addToBot(new ReducePowerAction(this.owner, this.owner, this, 1));
            if (this.amount == 1) {
                this.flash();
                addToBot(new AbstractGameAction() {
                    @Override
                    public void update() {
                        for (int i = 0 ; i < cards ; i++) {
                            AbstractCard tmp = card.makeSameInstanceOf();
                            AbstractDungeon.player.limbo.addToBottom(tmp);
                            tmp.current_x = card.current_x;
                            tmp.current_y = card.current_y;
                            tmp.target_x = (float) Settings.WIDTH / 2.0F - 300.0F * Settings.scale;
                            tmp.target_y = (float)Settings.HEIGHT / 2.0F;

                            tmp.purgeOnUse = true;
                            AbstractDungeon.actionManager.addCardQueueItem(new CardQueueItem(tmp, true, card.energyOnUse, true, true), true);// 68
                        }
                        this.isDone = true;
                    }
                });
            }
        }
    }

    @Override
    public void atEndOfTurn(boolean isPlayer) {
        if (amount == 1) {
            flashTimer = 1f;
        }
    }
}
