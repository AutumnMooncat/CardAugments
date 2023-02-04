/*
package CardAugments.powers;

import CardAugments.CardAugmentsMod;
import CardAugments.util.TextureLoader;
import basemod.interfaces.CloneablePowerInterface;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.NonStackablePower;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class IcizePower extends AbstractPower implements CloneablePowerInterface, NonStackablePower {
    public static final String POWER_ID = CardAugmentsMod.makeID(IcizePower.class.getSimpleName());
    private static final PowerStrings TEXT = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);

    private final AbstractCard card;

    public IcizePower(AbstractCreature owner, AbstractCard card) {
        name = card.name + TEXT.NAME;
        ID = POWER_ID;

        this.owner = owner;
        this.card = card.makeSameInstanceOf();

        type = PowerType.BUFF;
        isTurnBased = false;

        region128 = new TextureAtlas.AtlasRegion(TextureLoader.getTexture("CardAugmentsResources/images/powers/" + IcizePower.class.getSimpleName() + "84.png"), 0, 0, 84, 84);
        region48 = new TextureAtlas.AtlasRegion(TextureLoader.getTexture("CardAugmentsResources/images/powers/" + IcizePower.class.getSimpleName() + "32.png"), 0, 0, 32, 32);

        updateDescription();
    }

    @Override
    public void updateDescription() {
        description = String.format(TEXT.DESCRIPTIONS[0], card.name);
    }

    @Override
    public void atStartOfTurnPostDraw() {
        card.use(AbstractDungeon.player, AbstractDungeon.getRandomMonster());
    }

    @Override
    public AbstractPower makeCopy() {
        return new IcizePower(owner, card);
    }
}
*/
