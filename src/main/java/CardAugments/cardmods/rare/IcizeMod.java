package CardAugments.cardmods.rare;

import CardAugments.CardAugmentsMod;
import CardAugments.cardmods.AbstractAugment;
import CardAugments.patches.InterruptUseCardFieldPatches;
import CardAugments.powers.IcizePower;
import CardAugments.util.PortraitHelper;
import basemod.AutoAdd;
import basemod.abstracts.AbstractCardModifier;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class IcizeMod extends AbstractAugment {
    public static final String ID = CardAugmentsMod.makeID(IcizeMod.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;

    @Override
    public void onInitialApplication(AbstractCard card) {
        if (card.baseDamage > 1) {
            modifyBaseStat(card, BuffType.DAMAGE, BuffScale.MAJOR_DEBUFF);
        }
        if (card.baseBlock > 1) {
            modifyBaseStat(card, BuffType.BLOCK, BuffScale.MAJOR_DEBUFF);
        }
        if (card.baseMagicNumber > 1) {
            modifyBaseStat(card, BuffType.MAGIC, BuffScale.MAJOR_DEBUFF);
        }
        card.type = AbstractCard.CardType.POWER;
//        card.target = AbstractCard.CardTarget.SELF;  // this breaks certain cards (because use() is still called for now)
        PortraitHelper.setMaskedPortrait(card);
        InterruptUseCardFieldPatches.InterceptUseField.interceptUse.set(card, true);
    }

    @Override
    public boolean validCard(AbstractCard card) {
        try {
            card.getClass().getDeclaredMethod("canUse", AbstractPlayer.class, AbstractMonster.class);
        } catch (NoSuchMethodException ignored) {
            return card.cost >= 0 &&
                    card.type != AbstractCard.CardType.POWER &&
                    !card.exhaust;
        }
        return false;
    }

    @Override
    public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
        addToBot(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new IcizePower(AbstractDungeon.player, card)));
    }

    @Override
    public String modifyName(String cardName, AbstractCard card) {
        return TEXT[0] + cardName + TEXT[1];
    }

    @Override
    public String modifyDescription(String rawDescription, AbstractCard card) {
        return TEXT[2] + rawDescription;
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.RARE;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new IcizeMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}
