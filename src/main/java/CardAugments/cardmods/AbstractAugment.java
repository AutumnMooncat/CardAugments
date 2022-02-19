package CardAugments.cardmods;

import basemod.abstracts.AbstractCardModifier;
import com.evacipated.cardcrawl.mod.stslib.fields.cards.AbstractCard.ExhaustiveField;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.colorless.PanicButton;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import javassist.ClassPool;
import javassist.CtMethod;
import javassist.expr.ExprEditor;
import javassist.expr.FieldAccess;
import org.apache.commons.lang3.StringUtils;

public abstract class AbstractAugment extends AbstractCardModifier {
    public enum AugmentRarity {
        COMMON,
        UNCOMMON,
        RARE
    }

    public enum BuffType {
        DAMAGE,
        BLOCK,
        MAGIC
    }

    public enum BuffScale {
        HUGE_BUFF,
        MAJOR_BUFF,
        MODERATE_BUFF,
        MINOR_BUFF,
        MINOR_DEBUFF,
        MODERATE_DEBUFF,
        MAJOR_DEBUFF,
        HUGE_DEBUFF,
    }

    public abstract AugmentRarity getModRarity();

    public abstract boolean validCard(AbstractCard card);

    public void onDamaged(AbstractCard c) {}

    @Override
    public boolean shouldApply(AbstractCard card) {
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

    public static void modifyBaseStat(AbstractCard card, BuffType type, BuffScale scaling) {
        AbstractCard upgradeCheck = card.makeCopy();
        upgradeCheck.upgrade();
        switch (type) {
            case DAMAGE:
                card.baseDamage += getStatModification(Math.max(card.baseDamage, upgradeCheck.baseDamage), scaling);
                if (card.baseDamage < 1) {
                    card.baseDamage = 1;
                }
                card.damage = card.baseDamage;
                break;
            case BLOCK:
                card.baseBlock += getStatModification(Math.max(card.baseBlock, upgradeCheck.baseBlock), scaling);
                if (card.baseBlock < 1) {
                    card.baseBlock = 1;
                }
                card.block = card.baseBlock;
                break;
            case MAGIC:
                card.baseMagicNumber += getStatModification(Math.max(card.baseMagicNumber, upgradeCheck.baseMagicNumber), scaling);
                if (card.baseMagicNumber < 1) {
                    card.baseMagicNumber = 1;
                }
                card.magicNumber = card.baseMagicNumber;
                break;
        }
    }

    public static int getStatModification(int baseStat, BuffScale scaling) {
        switch (scaling) {
            case HUGE_BUFF:
                return (int) Math.ceil(baseStat/2f);
            case MAJOR_BUFF:
                return (int) Math.ceil(baseStat/3f);
            case MODERATE_BUFF:
                return (int) Math.ceil(baseStat/4f);
            case MINOR_BUFF:
                return (int) Math.ceil(baseStat/5f);
            case MINOR_DEBUFF:
                return (int) -Math.ceil(baseStat/5f);
            case MODERATE_DEBUFF:
                return (int) -Math.ceil(baseStat/4f);
            case MAJOR_DEBUFF:
                return (int) -Math.ceil(baseStat/3f);
            case HUGE_DEBUFF:
                return (int) -Math.ceil(baseStat/2f);
        }
        return 0;
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

    public void onUpgradeCheck(AbstractCard card) {}

    public void updateDynvar(AbstractCard card) {}
}
