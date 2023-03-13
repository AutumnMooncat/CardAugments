package CardAugments.cardmods.uncommon;

import CardAugments.CardAugmentsMod;
import CardAugments.cardmods.AbstractAugment;
import basemod.abstracts.AbstractCardModifier;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class MasochisticMod extends AbstractAugment {
    public static final String ID = CardAugmentsMod.makeID(MasochisticMod.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;

    private static final int BOOST = 3;

    @Override
    public boolean validCard(AbstractCard card) {
        return card.baseBlock > 0;
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
        return rawDescription + String.format(TEXT[2], BOOST);
    }

    @Override
    public float modifyBlock(float block, AbstractCard card) {
        return block + (BOOST * AbstractDungeon.player.powers.stream().filter(p -> p.type == AbstractPower.PowerType.DEBUFF).count());
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.UNCOMMON;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new MasochisticMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }

}
