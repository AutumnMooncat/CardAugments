package CardAugments.cardmods.uncommon;

import CardAugments.CardAugmentsMod;
import CardAugments.cardmods.AbstractAugment;
import basemod.abstracts.AbstractCardModifier;
import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;

public class MementoMod extends AbstractAugment {
    public static final String ID = CardAugmentsMod.makeID(MementoMod.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;

    private static final int AMOUNT = 2;

    @Override
    public void onInitialApplication(AbstractCard card) {
        card.isEthereal = true;
    }

    @Override
    public boolean validCard(AbstractCard card) {
        return card.cost != -2 && card.rarity != AbstractCard.CardRarity.BASIC && cardCheck(card, c -> notExhaust(c) && notEthereal(c));
    }

    @Override
    public String getPrefix() {
        return TEXT[0];
    }

    @Override
    public String getSuffix() {
        return TEXT[1];
    }

    @Override
    public String modifyDescription(String rawDescription, AbstractCard card) {
        return TEXT[2] + rawDescription + String.format(TEXT[3], AMOUNT);
    }

    @Override
    public void onExhausted(AbstractCard card) {
        card.flash(Color.RED.cpy());
        this.addToBot(new DrawCardAction(AMOUNT));
        /*for (AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
            if (!m.isDeadOrEscaped()) {
                VulnerablePower v = new VulnerablePower(m, AMOUNT, true);
                ReflectionHacks.setPrivate(v, VulnerablePower.class, "justApplied", true);
                this.addToBot(new ApplyPowerAction(m, AbstractDungeon.player, v, AMOUNT, true));
            }
        }*/
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.UNCOMMON;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new MementoMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}
