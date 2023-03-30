package CardAugments.cardmods.rare;

import CardAugments.CardAugmentsMod;
import CardAugments.cardmods.AbstractAugment;
import basemod.abstracts.AbstractCardModifier;
import com.megacrit.cardcrawl.actions.defect.ChannelAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.orbs.Plasma;

public class MeteorMod extends AbstractAugment {
    public static final String ID = CardAugmentsMod.makeID(MeteorMod.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;

    private int increase;

    @Override
    public void onInitialApplication(AbstractCard card) {
        increase = 5 - card.cost;
        card.cost = 5;
        card.costForTurn = 5;
    }

    @Override
    public float modifyBaseDamage(float damage, DamageInfo.DamageType type, AbstractCard card, AbstractMonster target) {
        return damage * (1 + 0.25f * increase);
    }

    @Override
    public boolean validCard(AbstractCard card) {
        return allowOrbMods() && card.rarity != AbstractCard.CardRarity.BASIC && (card.cost >= 1 && card.cost <= 4) && card.baseDamage > 0 && card.type == AbstractCard.CardType.ATTACK && cardCheck(card, c -> doesntUpgradeCost());
    }

    @Override
    public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
        for(int i = 0; i < increase; ++i) {
            this.addToBot(new ChannelAction(new Plasma()));
        }
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
        return rawDescription + String.format(TEXT[2], increase);
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.RARE;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new MeteorMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}
