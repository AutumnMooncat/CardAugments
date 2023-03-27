package CardAugments.cardmods.uncommon;

import CardAugments.CardAugmentsMod;
import CardAugments.actions.ImmediateExhaustCardAction;
import CardAugments.cardmods.AbstractAugment;
import Starlight.util.Wiz;
import basemod.abstracts.AbstractCardModifier;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class VanishingMod extends AbstractAugment {
    public static final String ID = CardAugmentsMod.makeID(VanishingMod.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;

    boolean modMagic;

    @Override
    public void onInitialApplication(AbstractCard card) {
        if (cardCheck(card, c -> doesntDowngradeMagic() && c.baseMagicNumber >= 3)) {
            modMagic = true;
        }
    }

    @Override
    public float modifyBaseDamage(float damage, DamageInfo.DamageType type, AbstractCard card, AbstractMonster target) {
        if (card.baseDamage > 0) {
            return damage * MAJOR_BUFF;
        }
        return damage;
    }

    @Override
    public float modifyBaseBlock(float block, AbstractCard card) {
        if (card.baseBlock > 0) {
            return block * MAJOR_BUFF;
        }
        return block;
    }

    @Override
    public float modifyBaseMagic(float magic, AbstractCard card) {
        if (modMagic) {
            return magic * MAJOR_BUFF;
        }
        return magic;
    }

    @Override
    public boolean validCard(AbstractCard card) {
        return doesntOverride(card, "tookDamage") && (card.baseDamage >= 3 || card.baseBlock >= 3 || cardCheck(card, c -> doesntDowngradeMagic() && c.baseMagicNumber >= 3));
    }

    @Override
    public void onDamaged(AbstractCard c) {
        Wiz.atb(new ImmediateExhaustCardAction(c));
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
        return rawDescription + TEXT[2];
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.UNCOMMON;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new VanishingMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}
