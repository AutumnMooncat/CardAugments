package CardAugments.powers;

import CardAugments.CardAugmentsMod;
import CardAugments.util.TextureLoader;
import basemod.interfaces.CloneablePowerInterface;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class RunicPower extends AbstractPower implements CloneablePowerInterface {
    public static final String POWER_ID = CardAugmentsMod.makeID(RunicPower.class.getSimpleName());
    private static final PowerStrings TEXT = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);

    public RunicPower(AbstractCreature owner, int amount) {
        name = TEXT.NAME;
        ID = POWER_ID;

        this.owner = owner;
        this.amount = amount;

        type = PowerType.DEBUFF;
        isTurnBased = true;

        region128 = new TextureAtlas.AtlasRegion(TextureLoader.getTexture("CardAugmentsResources/images/powers/blahblah84.png"), 0, 0, 84, 84);
        region48 = new TextureAtlas.AtlasRegion(TextureLoader.getTexture("CardAugmentsResources/images/powers/blahblah32.png"), 0, 0, 32, 32);

        updateDescription();
    }

    @Override
    public void updateDescription() {
        description = TEXT.DESCRIPTIONS[0];
    }

    @Override
    public void atEndOfTurn(boolean isPlayer) {
        if (isPlayer)
            addToBot(new ReducePowerAction(owner, owner, this, 1));
    }

    @Override
    public AbstractPower makeCopy() {
        return new RunicPower(owner, amount);
    }
}
