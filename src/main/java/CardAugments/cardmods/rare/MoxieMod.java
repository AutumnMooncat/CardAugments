package CardAugments.cardmods.rare;

import CardAugments.CardAugmentsMod;
import CardAugments.cardmods.AbstractAugment;
import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import com.evacipated.cardcrawl.mod.stslib.damagemods.AbstractDamageModifier;
import com.evacipated.cardcrawl.mod.stslib.damagemods.DamageModifierManager;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.GetAllInBattleInstances;
import com.megacrit.cardcrawl.powers.MinionPower;

public class MoxieMod extends AbstractAugment {
    public static final String ID = CardAugmentsMod.makeID("MoxieMod");
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;

    private static final int BOOST = 2;

    @Override
    public void onInitialApplication(AbstractCard card) {
        super.onInitialApplication(card);
        DamageModifierManager.addModifier(card, new MoxieDamage(BOOST));
    }

    @Override
    public boolean validCard(AbstractCard card) {
        return card.type == AbstractCard.CardType.ATTACK && card.baseDamage > 0 && cardDoesntExhaust(card);
    }

    @Override
    public String modifyName(String cardName, AbstractCard card) {
        return TEXT[0] + cardName + TEXT[1];
    }

    @Override
    public String modifyDescription(String rawDescription, AbstractCard card) {
        return rawDescription + String.format(TEXT[2], BOOST);
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.RARE;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new MoxieMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }

    public static class MoxieDamage extends AbstractDamageModifier {
        int boost;

        public MoxieDamage(int boost) {
            this.priority = Short.MAX_VALUE;
            this.boost = boost;
        }

        @Override
        public void onLastDamageTakenUpdate(DamageInfo info, int lastDamageTaken, int overkillAmount, AbstractCreature targetHit) {
            if (DamageModifierManager.getInstigator(info) instanceof AbstractCard) {
                AbstractCard c = (AbstractCard) DamageModifierManager.getInstigator(info);
                if (targetHit.currentHealth > 0 && targetHit.currentHealth - lastDamageTaken <= 0 && !targetHit.halfDead && !targetHit.hasPower(MinionPower.POWER_ID)) {
                    for (AbstractCard card : AbstractDungeon.player.masterDeck.group) {
                        if (card.uuid.equals(c.uuid)) {
                            CardModifierManager.addModifier(card, new MoxieBoost(boost));
                        }
                    }

                    for(AbstractCard card : GetAllInBattleInstances.get(c.uuid)) {
                        CardModifierManager.addModifier(card, new MoxieBoost(boost));
                    }
                }
            }
        }

        @Override
        public boolean isInherent() {
            return true;
        }

        @Override
        public AbstractDamageModifier makeCopy() {
            return new MoxieDamage(boost);
        }
    }

    public static class MoxieBoost extends AbstractCardModifier {
        public static final String ID = CardAugmentsMod.makeID("MoxieBoost");
        int amount;

        public MoxieBoost(int amount) {
            this.amount = amount;
        }

        @Override
        public void onInitialApplication(AbstractCard card) {
            card.baseDamage += amount;
            card.damage = card.baseDamage;
        }

        @Override
        public boolean shouldApply(AbstractCard card) {
            if (CardModifierManager.hasModifier(card, identifier(card))) {
                MoxieBoost m = (MoxieBoost) CardModifierManager.getModifiers(card, identifier(card)).get(0);
                m.amount += amount;
                card.baseDamage += amount;
                card.damage = card.baseDamage;
                return false;
            }
            return true;
        }

        @Override
        public String identifier(AbstractCard card) {
            return ID;
        }

        @Override
        public AbstractCardModifier makeCopy() {
            return new MoxieBoost(amount);
        }
    }
}
