package CardAugments.powers;

import CardAugments.CardAugmentsMod;
import CardAugments.util.TextureLoader;
import basemod.interfaces.CloneablePowerInterface;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.NonStackablePower;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

import java.lang.reflect.InvocationTargetException;

public class BiasPower extends AbstractPower implements CloneablePowerInterface, NonStackablePower {
    public static final String POWER_ID = CardAugmentsMod.makeID(BiasPower.class.getSimpleName());
    private static final PowerStrings TEXT = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);

    private final String targetID;
    private final String targetName;
    private final Class<? extends AbstractPower> targetClass;
    private final boolean targetCanGoNegative;

    public BiasPower(AbstractCreature owner, int amount, AbstractPower target) {
        this(owner, amount, target.ID, target.name, target.getClass(), target.canGoNegative);
    }

    private BiasPower(AbstractCreature owner, int amount, String targetID, String targetName, Class<? extends AbstractPower> targetClass, boolean targetCanGoNegative) {
        name = TEXT.NAME;
        ID = POWER_ID;
        canGoNegative = true;

        this.owner = owner;
        this.amount = -amount;
        this.targetID = targetID;
        this.targetName = targetName;
        this.targetClass = targetClass;
        this.targetCanGoNegative = targetCanGoNegative;

        type = PowerType.DEBUFF;

        region128 = new TextureAtlas.AtlasRegion(TextureLoader.getTexture("CardAugmentsResources/images/powers/" + BiasPower.class.getSimpleName() + "84.png"), 0, 0, 84, 84);
        region48 = new TextureAtlas.AtlasRegion(TextureLoader.getTexture("CardAugmentsResources/images/powers/" + BiasPower.class.getSimpleName() + "32.png"), 0, 0, 32, 32);

        updateDescription();
    }

    @Override
    public void updateDescription() {
        description = String.format(TEXT.DESCRIPTIONS[0], -amount, targetName);
    }

    @Override
    public void atStartOfTurn() {
        this.flash();
        if (targetCanGoNegative) {
            try {
                addToBot(new ApplyPowerAction(owner, owner, targetClass.getDeclaredConstructor(AbstractCreature.class, int.class).newInstance(owner, amount), amount));
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                addToBot(new ReducePowerAction(owner, owner, targetID, -amount));
            }
        } else
            addToBot(new ReducePowerAction(owner, owner, targetID, -amount));

    }

    @Override
    public AbstractPower makeCopy() {
        return new BiasPower(owner, amount, targetID, targetName, targetClass, canGoNegative);
    }
}
