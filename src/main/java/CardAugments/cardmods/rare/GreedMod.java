package CardAugments.cardmods.rare;

import CardAugments.CardAugmentsMod;
import CardAugments.cardmods.AbstractAugment;
import basemod.abstracts.AbstractCardModifier;
import com.evacipated.cardcrawl.mod.stslib.damagemods.AbstractDamageModifier;
import com.evacipated.cardcrawl.mod.stslib.damagemods.DamageModifierManager;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.MinionPower;

public class GreedMod extends AbstractAugment {
    public static final String ID = CardAugmentsMod.makeID("GreedMod");
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;

    private static final int GOLD = 15;

    @Override
    public void onInitialApplication(AbstractCard card) {
        super.onInitialApplication(card);
        DamageModifierManager.addModifier(card, new GreedDamage(GOLD));
        card.exhaust = true;
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
        return rawDescription + String.format(TEXT[2], GOLD);
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.RARE;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new GreedMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }

    public static class GreedDamage extends AbstractDamageModifier {
        int gold;

        public GreedDamage(int gold) {
            this.priority = Short.MAX_VALUE;
            this.gold = gold;
        }

        @Override
        public void onLastDamageTakenUpdate(DamageInfo info, int lastDamageTaken, int overkillAmount, AbstractCreature targetHit) {
            if (DamageModifierManager.getInstigator(info) instanceof AbstractCard) {
                if (targetHit.currentHealth > 0 && targetHit.currentHealth - lastDamageTaken <= 0 && !targetHit.halfDead && !targetHit.hasPower(MinionPower.POWER_ID)) {
                    AbstractDungeon.player.gainGold(gold);
                }
            }
        }

        @Override
        public boolean isInherent() {
            return true;
        }

        @Override
        public AbstractDamageModifier makeCopy() {
            return new GreedDamage(gold);
        }
    }
}
