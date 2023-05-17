package CardAugments.cardmods.rare;

import CardAugments.CardAugmentsMod;
import CardAugments.cardmods.AbstractAugment;
import CardAugments.cardmods.DynvarCarrier;
import CardAugments.util.CalcHelper;
import CardAugments.util.Wiz;
import basemod.abstracts.AbstractCardModifier;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;

import java.util.ArrayList;
import java.util.Iterator;

public class SluggerMod extends AbstractAugment implements DynvarCarrier {
    public static final String ID = CardAugmentsMod.makeID(SluggerMod.class.getSimpleName());
    public static final String DESCRIPTION_KEY = "!"+ID+"!";
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;
    public static final String[] CARD_TEXT = CardCrawlGame.languagePack.getUIString(ID).EXTRA_TEXT;

    private static final int AMOUNT = 6;
    private static final int UPGRADE_AMOUNT = 3;

    public int val;
    public int[] multiVal;
    public boolean modified;
    public boolean upgraded;
    public boolean addedExhaust;

    public int getBaseVal(AbstractCard card) {
        return AMOUNT + getEffectiveUpgrades(card) * UPGRADE_AMOUNT;
    }

    @Override
    public void onInitialApplication(AbstractCard card) {
        val = getBaseVal(card);
        addedExhaust = !card.exhaust;
        card.exhaust = true;
    }

    @Override
    public void updateDynvar(AbstractCard card) {
        val = getBaseVal(card);
        modified = false;
    }

    @Override
    public void onApplyPowers(AbstractCard card) {
        multiVal = CalcHelper.applyPowersMulti(getBaseVal(card));
        val = multiVal[0];
        modified = val != getBaseVal(card);
    }

    @Override
    public void onCalculateCardDamage(AbstractCard card, AbstractMonster mo) {
        multiVal = CalcHelper.calculateCardDamageMulti(getBaseVal(card));
        val = multiVal[0];
        modified = val != getBaseVal(card);
    }

    @Override
    public boolean validCard(AbstractCard card) {
        return card.type == AbstractCard.CardType.ATTACK && card.baseDamage > 0;
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
    public String getAugmentDescription() {
        return TEXT[2];
    }

    @Override
    public String modifyDescription(String rawDescription, AbstractCard card) {
        if (addedExhaust) {
            return insertAfterText(rawDescription , String.format(CARD_TEXT[1], DESCRIPTION_KEY));
        }
        return insertAfterText(rawDescription , String.format(CARD_TEXT[0], DESCRIPTION_KEY));
    }

    @Override
    public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
        //Bonus safety
        multiVal = CalcHelper.calculateCardDamageMulti(getBaseVal(card));
        ArrayList<AbstractMonster> enemies = Wiz.getEnemies();
        for (AbstractMonster m : enemies) {
            boolean last = enemies.indexOf(m) == enemies.size()-1;
            addToBot(new DamageAction(m, new DamageInfo(AbstractDungeon.player, multiVal[AbstractDungeon.getMonsters().monsters.indexOf(m)], DamageInfo.DamageType.NORMAL), last ? AbstractGameAction.AttackEffect.BLUNT_HEAVY : AbstractGameAction.AttackEffect.BLUNT_LIGHT, last));
        }
        addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                for (AbstractPower p : AbstractDungeon.player.powers) {
                    p.onDamageAllEnemies(multiVal);
                }
                this.isDone = true;
            }
        });
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.RARE;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new SluggerMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }

    @Override
    public String key() {
        return ID;
    }

    @Override
    public int val(AbstractCard card) {
        return val;
    }

    @Override
    public int baseVal(AbstractCard card) {
        return getBaseVal(card);
    }

    @Override
    public boolean modified(AbstractCard card) {
        return modified;
    }

    @Override
    public boolean upgraded(AbstractCard card) {
        val = getBaseVal(card);
        modified = card.upgraded;
        upgraded = card.upgraded;
        return upgraded;
    }
}
