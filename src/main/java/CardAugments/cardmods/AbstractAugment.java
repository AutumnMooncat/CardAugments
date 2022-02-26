package CardAugments.cardmods;

import CardAugments.CardAugmentsMod;
import CardAugments.patches.InfiniteUpgradesPatches;
import basemod.abstracts.AbstractCardModifier;
import com.evacipated.cardcrawl.mod.stslib.fields.cards.AbstractCard.ExhaustiveField;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.colorless.PanicButton;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.PrismaticShard;
import javassist.ClassPool;
import javassist.CtMethod;
import javassist.expr.ExprEditor;
import javassist.expr.FieldAccess;
import org.apache.commons.lang3.StringUtils;

public abstract class AbstractAugment extends AbstractCardModifier {
    public enum AugmentRarity {
        COMMON,
        UNCOMMON,
        RARE,
        SPECIAL
    }

    public enum BuffType {
        DAMAGE,
        BLOCK,
        MAGIC
    }

    public enum BuffScale {
        HUGE_BUFF(1/2F),
        MAJOR_BUFF(1/3F),
        MODERATE_BUFF(1/4F),
        MINOR_BUFF(1/5F),
        MINOR_DEBUFF(-1/5F),
        MODERATE_DEBUFF(-1/4F),
        MAJOR_DEBUFF(-1/3F),
        HUGE_DEBUFF(-1/2F);
        private final float multi;
        BuffScale(final float multi) {
            this.multi = multi;
        }
        public float getMulti() {
            return multi;
        }
    }

    public abstract AugmentRarity getModRarity();

    public abstract boolean validCard(AbstractCard card);

    public void onDamaged(AbstractCard c) {}

    public void onUpgradeCheck(AbstractCard card) {}

    public void updateDynvar(AbstractCard card) {}

    public boolean glowCheck(AbstractCard card) {
        return false;
    }

    public boolean canRoll(AbstractCard card) {
        if (!validCard(card)) {
            return false;
        }
        AbstractCard upgradeCheck = card.makeCopy();
        upgradeCheck.upgrade();
        return validCard(upgradeCheck);
    }

    protected void addToBot(AbstractGameAction action) {
        AbstractDungeon.actionManager.addToBottom(action);
    }

    protected void addToTop(AbstractGameAction action) {
        AbstractDungeon.actionManager.addToTop(action);
    }

    public static boolean isNormalCard(AbstractCard card) {
        return card.type != AbstractCard.CardType.CURSE && card.type != AbstractCard.CardType.STATUS;
    }

    public static boolean cardDoesntExhaust(AbstractCard card) {
        return !card.exhaust && !card.purgeOnUse && ExhaustiveField.ExhaustiveFields.baseExhaustive.get(card) == -1 && ExhaustiveField.ExhaustiveFields.exhaustive.get(card) == -1;
    }

    public static void modifyBaseStat(AbstractCard card, BuffType type, float buffMulti) {
        AbstractCard deltaCheck = card.makeCopy();
        if (InfiniteUpgradesPatches.InfUpgradeField.inf.get(card)) {
            InfiniteUpgradesPatches.InfUpgradeField.inf.set(deltaCheck, true); // Needed for while upgrading check to not explode
        }
        int discrepancy; //Stores a discrepancy with the new card (from previous damage modifications)
        int baseVal;
        int upgradeVal;
        switch (type) {
            case DAMAGE:
                baseVal = deltaCheck.baseDamage; //Store the original unedited base value of a fresh copy
                while (deltaCheck.timesUpgraded < card.timesUpgraded) { //Make our new copy as many times upgraded as our actual card
                    deltaCheck.upgrade();
                }
                discrepancy = card.baseDamage - deltaCheck.baseDamage; //Determine the difference in damage. This can be caused by calling modifyBaseStat more than once
                baseVal += discrepancy; //Add this discrepancy to our base val. This now stores what an upgraded proper copy of our card would have
                if (deltaCheck.timesUpgraded == 0) { //If we didn't actually upgrade the card, we need to do so to see its upgraded value
                    deltaCheck.upgrade();
                }
                upgradeVal = deltaCheck.baseDamage + discrepancy; //Determine what the upgraded value of our proper copy would be. We again add the discrepancy with our real card
                card.baseDamage += Math.ceil(Math.max(baseVal, upgradeVal)*buffMulti); //We need to compare upgraded and not upgraded in case someone makes a card that lowers the value on upgrade
                if (card.baseDamage < 1) {
                    card.baseDamage = 1;
                }
                card.damage = card.baseDamage;
                break;
            case BLOCK:
                baseVal = deltaCheck.baseBlock;
                while (deltaCheck.timesUpgraded < card.timesUpgraded) {
                    deltaCheck.upgrade();
                }
                discrepancy = card.baseBlock - deltaCheck.baseBlock;
                baseVal += discrepancy;
                if (deltaCheck.timesUpgraded == 0) {
                    deltaCheck.upgrade();
                }
                upgradeVal = deltaCheck.baseBlock + discrepancy;
                card.baseBlock += Math.ceil(Math.max(baseVal, upgradeVal)*buffMulti);
                if (card.baseBlock < 1) {
                    card.baseBlock = 1;
                }
                card.block = card.baseBlock;
                break;
            case MAGIC:
                baseVal = deltaCheck.baseMagicNumber;
                while (deltaCheck.timesUpgraded < card.timesUpgraded) {
                    deltaCheck.upgrade();
                }
                discrepancy = card.baseMagicNumber - deltaCheck.baseMagicNumber;
                baseVal += discrepancy;
                if (deltaCheck.timesUpgraded == 0) {
                    deltaCheck.upgrade();
                }
                upgradeVal = deltaCheck.baseMagicNumber + discrepancy;
                card.baseMagicNumber += Math.ceil(Math.max(baseVal, upgradeVal)*buffMulti);
                if (card.baseMagicNumber < 1) {
                    card.baseMagicNumber = 1;
                }
                card.magicNumber = card.baseMagicNumber;
                break;
        }
    }

    public static void modifyBaseStat(AbstractCard card, BuffType type, BuffScale scaling) {
        modifyBaseStat(card, type, scaling.getMulti());
    }

    private static boolean usesMagic;

    public static boolean usesMagic(AbstractCard card) {
        usesMagic = false;
        if (card.baseMagicNumber > 0 && StringUtils.containsIgnoreCase(card.rawDescription, "!M!") && !(card instanceof PanicButton)) {
            try {
                ClassPool pool = Loader.getClassPool();
                CtMethod ctClass = pool.get(card.getClass().getName()).getDeclaredMethod("use");

                ctClass.instrument(new ExprEditor() {
                    @Override
                    public void edit(FieldAccess f) {

                        if (f.getFieldName().equals("magicNumber") && !f.isWriter()) {
                            usesMagic = true;
                        }

                    }
                });

            } catch (Exception ignored) { }
        }
        return usesMagic;
    }

    public static boolean allowOrbMods() {
        return CardAugmentsMod.allowOrbs || AbstractDungeon.player.hasRelic(PrismaticShard.ID) || CardAugmentsMod.ORB_CHARS.contains(AbstractDungeon.player.chosenClass);
    }
}
