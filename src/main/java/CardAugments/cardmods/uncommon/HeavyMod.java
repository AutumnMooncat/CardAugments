package CardAugments.cardmods.uncommon;

import CardAugments.CardAugmentsMod;
import CardAugments.cardmods.AbstractAugment;
import basemod.abstracts.AbstractCardModifier;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.red.HeavyBlade;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.StrengthPower;

public class HeavyMod extends AbstractAugment {
    public static final String ID = CardAugmentsMod.makeID(HeavyMod.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;

    private static final int BOOST = 3;

    @Override
    public boolean validCard(AbstractCard card) {
        return card.type == AbstractCard.CardType.ATTACK && card.baseDamage > 0;
    }

    @Override
    public void onInitialApplication(AbstractCard card) {
        if (card instanceof HeavyBlade) {
            card.magicNumber += BOOST;
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
        if (card instanceof HeavyBlade) {
            return rawDescription;
        }
        return rawDescription + String.format(TEXT[2], BOOST);
    }

    @Override
    public float modifyDamage(float damage, DamageInfo.DamageType type, AbstractCard card, AbstractMonster target) {
        if (!(card instanceof HeavyBlade) && AbstractDungeon.player.hasPower(StrengthPower.POWER_ID)) {
            return damage + (AbstractDungeon.player.getPower(StrengthPower.POWER_ID).amount * (BOOST-1));
        }
        return damage;
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.UNCOMMON;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new HeavyMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }

}
