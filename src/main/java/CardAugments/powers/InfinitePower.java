package CardAugments.powers;

import CardAugments.CardAugmentsMod;
import CardAugments.util.FormatHelper;
import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.NonStackablePower;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class InfinitePower extends AbstractPower implements NonStackablePower {
    public static final String POWER_ID = CardAugmentsMod.makeID(InfinitePower.class.getSimpleName());
    private static final PowerStrings TEXT = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    private final AbstractCard card;

    public InfinitePower(AbstractCreature owner, AbstractCard card) {
        name = TEXT.NAME + card.name;
        ID = POWER_ID;
        this.owner = owner;
        this.card = card.makeStatEquivalentCopy();
        type = PowerType.BUFF;
        isTurnBased = false;
        this.loadRegion("infiniteBlades");
        updateDescription();
    }

    @Override
    public void updateDescription() {
        description = String.format(TEXT.DESCRIPTIONS[0], FormatHelper.prefixWords(card.name, "#y"));
    }

    @Override
    public void atStartOfTurnPostDraw() {
        addToBot(new MakeTempCardInHandAction(card.makeStatEquivalentCopy()));
    }
}
