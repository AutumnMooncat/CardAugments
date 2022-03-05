package CardAugments.powers;

import CardAugments.CardAugmentsMod;
import CardAugments.cutStuff.InfiniteMod;
import CardAugments.cardmods.util.InfiniteGeneratedMod;
import CardAugments.util.TextureLoader;
import basemod.helpers.CardModifierManager;
import basemod.interfaces.CloneablePowerInterface;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.NonStackablePower;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class InfinitePower extends AbstractPower implements CloneablePowerInterface, NonStackablePower {
    public static final String POWER_ID = CardAugmentsMod.makeID(InfinitePower.class.getSimpleName());
    private static final PowerStrings TEXT = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);

    private final AbstractCard card;

    public InfinitePower(AbstractCreature owner, AbstractCard card) {
        name = TEXT.NAME + card.name + TEXT.DESCRIPTIONS[0];
        ID = POWER_ID;

        this.owner = owner;
        this.card = card.makeStatEquivalentCopy();
        CardModifierManager.removeModifiersById(this.card, InfiniteMod.ID, true);
        CardModifierManager.addModifier(this.card, new InfiniteGeneratedMod());

        type = PowerType.BUFF;
        isTurnBased = false;

        region128 = new TextureAtlas.AtlasRegion(TextureLoader.getTexture("CardAugmentsResources/images/powers/" + InfinitePower.class.getSimpleName() + "84.png"), 0, 0, 84, 84);
        region48 = new TextureAtlas.AtlasRegion(TextureLoader.getTexture("CardAugmentsResources/images/powers/" + InfinitePower.class.getSimpleName() + "32.png"), 0, 0, 32, 32);

        updateDescription();
    }

    @Override
    public void updateDescription() {
        description = String.format(TEXT.DESCRIPTIONS[1], card.name);
    }

    @Override
    public void atStartOfTurnPostDraw() {
        addToBot(new MakeTempCardInHandAction(card.makeStatEquivalentCopy()));
    }

    @Override
    public AbstractPower makeCopy() {
        return new InfinitePower(owner, card);
    }
}
