package CardAugments.cardmods.rare;

import CardAugments.CardAugmentsMod;
import CardAugments.cardmods.AbstractAugment;
import basemod.abstracts.AbstractCardModifier;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class DemurMod extends AbstractAugment {
    public static final String ID = CardAugmentsMod.makeID(DemurMod.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;

    @Override
    public float modifyBaseBlock(float block, AbstractCard card) {
        return block * 2f;
    }

    @Override
    public boolean validCard(AbstractCard card) {
        return card.type == AbstractCard.CardType.SKILL && card.baseBlock > 0 && doesntOverride(card, "canUse", AbstractPlayer.class, AbstractMonster.class);
    }

    @Override
    public String getPrefix() {
        return TEXT[0];
    }

    @Override
    public String getSufix() {
        return TEXT[1];
    }

    @Override
    public String modifyDescription(String rawDescription, AbstractCard card) {
        return TEXT[2] + rawDescription;
    }

    @Override
    public boolean canPlayCard(AbstractCard card) {
        for (AbstractCard c : AbstractDungeon.player.hand.group) {
            if (c.type != AbstractCard.CardType.SKILL) {
                return false;
            }
        }
        return true;
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.RARE;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new DemurMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}
