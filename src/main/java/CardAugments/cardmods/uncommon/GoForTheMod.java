package CardAugments.cardmods.uncommon;

import CardAugments.CardAugmentsMod;
import CardAugments.cardmods.AbstractAugment;
import CardAugments.cardmods.DynvarCarrier;
import CardAugments.patches.InterruptUseCardFieldPatches;
import CardAugments.util.CalcHelper;
import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardBorderGlowManager;
import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.GameDictionary;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class GoForTheMod extends AbstractAugment implements DynvarCarrier {
    public static final String ID = CardAugmentsMod.makeID(GoForTheMod.class.getSimpleName());
    public static final String DESCRIPTION_KEY = "!"+ID+"!";
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;

    private static final int DAMAGE = 3;
    private static final int UPGRADE_DAMAGE = 1;

    public int val;
    public boolean modified;
    public boolean upgraded;

    public int getBaseVal(AbstractCard card) {
        return DAMAGE + getEffectiveUpgrades(card) * UPGRADE_DAMAGE;
    }

    @Override
    public void onInitialApplication(AbstractCard card) {
        val = getBaseVal(card);
        card.cost = card.cost - 1;
        card.costForTurn = card.cost;
        InterruptUseCardFieldPatches.InterceptUseField.interceptUse.set(card, true);
        if (card.target != AbstractCard.CardTarget.SELF_AND_ENEMY && card.target != AbstractCard.CardTarget.ENEMY) {
            card.target = AbstractCard.CardTarget.ENEMY;
        }
    }

    @Override
    public float modifyBaseDamage(float damage, DamageInfo.DamageType type, AbstractCard card, AbstractMonster target) {
        return damage * MINOR_DEBUFF;
    }

    @Override
    public void updateDynvar(AbstractCard card) {
        val = getBaseVal(card);
        modified = false;
    }

    @Override
    public void onApplyPowers(AbstractCard card) {
        val = CalcHelper.applyPowers(getBaseVal(card));
        modified = val != getBaseVal(card);
    }

    @Override
    public void onCalculateCardDamage(AbstractCard card, AbstractMonster mo) {
        val = CalcHelper.calculateCardDamage(getBaseVal(card), mo);
        modified = val != getBaseVal(card);
    }

    @Override
    public boolean validCard(AbstractCard card) {
        return cardCheck(card, c -> c.cost > 0 && c.baseDamage > 0 && doesntUpgradeCost() && usesVanillaTargeting(c) && c.type == AbstractCard.CardType.ATTACK && c.rawDescription.chars().filter(ch -> ch == '.' || ch == '。').count() == 1);
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
        if (Character.isAlphabetic(rawDescription.charAt(0))) {
            String word = rawDescription.split(" ")[0].replaceAll("[^a-zA-Z0-9]", "");
            if (!GameDictionary.keywords.containsKey(word.toLowerCase())) {
                char c[] = rawDescription.toCharArray();
                c[0] = Character.toLowerCase(c[0]);
                rawDescription = new String(c);
            }
        }
        return String.format(TEXT[2], DESCRIPTION_KEY) + rawDescription;
    }

    @Override
    public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
        addToBot(new DamageAction(target, new DamageInfo(AbstractDungeon.player, val, card.damageTypeForTurn), AbstractGameAction.AttackEffect.SLASH_DIAGONAL));
        if (target instanceof AbstractMonster && ((AbstractMonster) target).getIntentBaseDmg() >= 0) {
            card.use(AbstractDungeon.player, target instanceof AbstractMonster ? (AbstractMonster) target : null);
        }
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.UNCOMMON;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new GoForTheMod();
    }

    @Override
    public CardBorderGlowManager.GlowInfo getGlowInfo() {
        return new CardBorderGlowManager.GlowInfo() {
            @Override
            public boolean test(AbstractCard abstractCard) {
                return hasThisMod(abstractCard) && AbstractDungeon.getMonsters().monsters.stream().anyMatch(m -> !m.isDeadOrEscaped() && m.getIntentBaseDmg() >= 0);
            }

            @Override
            public Color getColor(AbstractCard abstractCard) {
                return Color.GOLD.cpy();
            }

            @Override
            public String glowID() {
                return ID+"Glow";
            }
        };
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
        modified = card.timesUpgraded != 0 || card.upgraded;
        upgraded = card.timesUpgraded != 0 || card.upgraded;
        return upgraded;
    }
}
