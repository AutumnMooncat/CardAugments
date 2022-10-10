package CardAugments.cardmods;

import CardAugments.CardAugmentsMod;
import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardBorderGlowManager;
import basemod.helpers.CardModifierManager;
import com.evacipated.cardcrawl.mod.stslib.cards.interfaces.BranchingUpgradesCard;
import com.evacipated.cardcrawl.mod.stslib.fields.cards.AbstractCard.ExhaustiveField;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.colorless.PanicButton;
import com.megacrit.cardcrawl.cards.purple.Halt;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.relics.PrismaticShard;
import com.megacrit.cardcrawl.stances.AbstractStance;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.expr.ExprEditor;
import javassist.expr.FieldAccess;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;
import java.util.function.Predicate;

public abstract class AbstractAugment extends AbstractCardModifier {
    public static final float HUGE_BUFF = 3/2f;
    public static final float MAJOR_BUFF = 4/3f;
    public static final float MODERATE_BUFF = 5/4f;
    public static final float MINOR_BUFF = 6/5f;
    public static final float HUGE_DEBUFF = 1/2f;
    public static final float MAJOR_DEBUFF = 2/3f;
    public static final float MODERATE_DEBUFF = 3/4f;
    public static final float MINOR_DEBUFF = 4/5f;
    private static AbstractCard baseCheck;
    private static AbstractCard upgradeCheck;
    private static AbstractCard branchingCheck;

    public enum AugmentRarity {
        COMMON,
        UNCOMMON,
        RARE,
        SPECIAL
    }

    /*public enum BuffType {
        DAMAGE,
        BLOCK,
        MAGIC
    }

    public enum BuffScale {
        HUGE_BUFF(3/2F),
        MAJOR_BUFF(4/3F),
        MODERATE_BUFF(5/4F),
        MINOR_BUFF(6/5F),
        MINOR_DEBUFF(4/5F),
        MODERATE_DEBUFF(3/4F),
        MAJOR_DEBUFF(2/3F),
        HUGE_DEBUFF(1/2F);
        private final float multi;
        BuffScale(final float multi) {
            this.multi = multi;
        }
        public float getMulti() {
            return multi;
        }
    }*/

    public abstract AugmentRarity getModRarity();

    public abstract boolean validCard(AbstractCard card);

    public CardBorderGlowManager.GlowInfo getGlowInfo() {
        return null;
    }

    public boolean hasThisMod(AbstractCard card) {
        return CardModifierManager.modifiers(card).stream().anyMatch(m -> m.identifier(card).equals(identifier(card)));
    }

    public boolean lastCardPlayedCheck(Predicate<AbstractCard> p) {
        if (AbstractDungeon.actionManager.cardsPlayedThisCombat.isEmpty()) {
            return false;
        }
        return p.test(AbstractDungeon.actionManager.cardsPlayedThisCombat.get(AbstractDungeon.actionManager.cardsPlayedThisCombat.size() - 1));
    }

    public boolean betterCanPlay(AbstractCard cardWithThisMod, AbstractCard cardToCheck) {
        return true;
    }

    public void onDamaged(AbstractCard c) {}

    public void onUpgradeCheck(AbstractCard card) {}

    public void updateDynvar(AbstractCard card) {}

    public boolean canRoll(AbstractCard card) {
        return validCard(card);
    }

    private static void setCardChecks(AbstractCard card) {
        baseCheck = card.makeCopy();
        upgradeCheck = card.makeCopy();
        branchingCheck = card.makeCopy();
        if (upgradeCheck instanceof BranchingUpgradesCard) {
            ((BranchingUpgradesCard) upgradeCheck).setUpgradeType(BranchingUpgradesCard.UpgradeType.NORMAL_UPGRADE);
            ((BranchingUpgradesCard) branchingCheck).setUpgradeType(BranchingUpgradesCard.UpgradeType.BRANCH_UPGRADE);
        }
        upgradeCheck.upgrade();
        branchingCheck.upgrade();
    }

    public static boolean cardCheck(AbstractCard card, Predicate<AbstractCard> p) {
        setCardChecks(card);
        return p.test(card);
    }

    protected void addToBot(AbstractGameAction action) {
        AbstractDungeon.actionManager.addToBottom(action);
    }

    protected void addToTop(AbstractGameAction action) {
        AbstractDungeon.actionManager.addToTop(action);
    }

    /*public static void modifyBaseStat(AbstractCard card, BuffType type, float buffMulti) {
        AbstractCard deltaCheck = card.makeCopy();
        if (InfiniteUpgradesPatches.InfUpgradeField.inf.get(card)) {
            InfiniteUpgradesPatches.InfUpgradeField.inf.set(deltaCheck, true); // Needed for loop upgrading to work properly
        }
        int discrepancy; //Stores a discrepancy with the new card (from previous damage modifications)
        int baseVal;
        int upgradeVal;
        switch (type) {
            case DAMAGE:
                baseVal = deltaCheck.baseDamage; //Store the original unedited base value of a fresh copy
                for (int i = 0 ; i < Math.abs(card.timesUpgraded) ; i++) {
                    deltaCheck.upgrade();
                }
                discrepancy = card.baseDamage - deltaCheck.baseDamage; //Determine the difference in damage. This can be caused by calling modifyBaseStat more than once
                baseVal += discrepancy; //Add this discrepancy to our base val. This now stores what a non-upgraded proper copy of our card would have
                if (deltaCheck.timesUpgraded == 0) { //If we didn't actually upgrade the card, we need to do so to see its upgraded value
                    deltaCheck.upgrade();
                }
                upgradeVal = deltaCheck.baseDamage + discrepancy; //Determine what the upgraded value of a proper copy would be. We again add the discrepancy with our real card
                card.baseDamage += roundHelper(Math.max(baseVal, upgradeVal)*buffMulti); //We need to compare upgraded and not upgraded in case someone makes a card that lowers the value on upgrade
                if (card.baseDamage < 1) {
                    card.baseDamage = 1;
                }
                card.damage = card.baseDamage;
                break;
            case BLOCK:
                baseVal = deltaCheck.baseBlock;
                for (int i = 0 ; i < Math.abs(card.timesUpgraded) ; i++) {
                    deltaCheck.upgrade();
                }
                discrepancy = card.baseBlock - deltaCheck.baseBlock;
                baseVal += discrepancy;
                if (deltaCheck.timesUpgraded == 0) {
                    deltaCheck.upgrade();
                }
                upgradeVal = deltaCheck.baseBlock + discrepancy;
                card.baseBlock += roundHelper(Math.max(baseVal, upgradeVal)*buffMulti);
                if (card.baseBlock < 1) {
                    card.baseBlock = 1;
                }
                card.block = card.baseBlock;
                break;
            case MAGIC:
                baseVal = deltaCheck.baseMagicNumber;
                for (int i = 0 ; i < Math.abs(card.timesUpgraded) ; i++) {
                    deltaCheck.upgrade();
                }
                discrepancy = card.baseMagicNumber - deltaCheck.baseMagicNumber;
                baseVal += discrepancy;
                if (deltaCheck.timesUpgraded == 0) {
                    deltaCheck.upgrade();
                }
                upgradeVal = deltaCheck.baseMagicNumber + discrepancy;
                card.baseMagicNumber += roundHelper(Math.max(baseVal, upgradeVal)*buffMulti);
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

    private static double roundHelper(float val) {
        return val >= 0 ? Math.ceil(val) : Math.floor(val);
    }*/

    public static boolean upgradesAVariable() {
        return upgradesDamage() || upgradesBlock() || upgradesMagic();
    }

    public static boolean upgradesDamage() {
        return upgradeCheck.baseDamage > baseCheck.baseDamage || branchingCheck.baseDamage > baseCheck.baseDamage;
    }

    public static boolean upgradesBlock() {
        return upgradeCheck.baseBlock > baseCheck.baseBlock || branchingCheck.baseBlock > baseCheck.baseBlock;
    }

    public static boolean upgradesMagic() {
        return (upgradeCheck.baseMagicNumber > baseCheck.baseMagicNumber || branchingCheck.baseMagicNumber > baseCheck.baseMagicNumber) && usesMagic(baseCheck);
    }

    public static boolean doesntDowngradeMagic() {
        return baseCheck.baseMagicNumber <= upgradeCheck.baseMagicNumber && baseCheck.baseMagicNumber <= branchingCheck.baseMagicNumber && usesMagic(baseCheck);
    }

    public static boolean reachesDamage(int amount) {
        return baseCheck.baseDamage >= amount || upgradeCheck.baseDamage >= amount || branchingCheck.baseDamage >= amount;
    }

    public static boolean reachesBlock(int amount) {
        return baseCheck.baseBlock >= amount || upgradeCheck.baseBlock >= amount || branchingCheck.baseBlock >= amount;
    }

    public static boolean reachesMagic(int amount) {
        return (baseCheck.baseMagicNumber >= amount || upgradeCheck.baseMagicNumber >= amount || branchingCheck.baseMagicNumber >= amount) && usesMagic(baseCheck);
    }

    public static boolean doesntUpgradeCost() {
        return baseCheck.cost == upgradeCheck.cost  && baseCheck.cost == branchingCheck.cost;
    }

    public static boolean notExhaust(AbstractCard card) {
        return doesntExhaust(card) && doesntUpgradeExhaust();
    }

    public static boolean doesntUpgradeExhaust() {
        return baseCheck.exhaust == upgradeCheck.exhaust && baseCheck.exhaust == branchingCheck.exhaust
                && baseCheck.purgeOnUse == upgradeCheck.purgeOnUse && baseCheck.purgeOnUse == branchingCheck.purgeOnUse
                && Objects.equals(ExhaustiveField.ExhaustiveFields.baseExhaustive.get(baseCheck), ExhaustiveField.ExhaustiveFields.baseExhaustive.get(upgradeCheck))
                && Objects.equals(ExhaustiveField.ExhaustiveFields.baseExhaustive.get(baseCheck), ExhaustiveField.ExhaustiveFields.baseExhaustive.get(branchingCheck))
                && Objects.equals(ExhaustiveField.ExhaustiveFields.exhaustive.get(baseCheck), ExhaustiveField.ExhaustiveFields.exhaustive.get(upgradeCheck))
                && Objects.equals(ExhaustiveField.ExhaustiveFields.exhaustive.get(baseCheck), ExhaustiveField.ExhaustiveFields.exhaustive.get(branchingCheck));
    }

    public static boolean notEthereal(AbstractCard card) {
        return !card.isEthereal && doesntUpgradeEthereal();
    }

    public static boolean doesntUpgradeEthereal() {
        return baseCheck.isEthereal == upgradeCheck.isEthereal && baseCheck.isEthereal == branchingCheck.isEthereal;
    }

    public static boolean notInnate(AbstractCard card) {
        return !card.isInnate && doesntUpgradeInnate();
    }

    public static boolean doesntUpgradeInnate() {
        return baseCheck.isInnate == upgradeCheck.isInnate && baseCheck.isInnate == branchingCheck.isInnate;
    }

    public static boolean notRetain(AbstractCard card) {
        return !card.selfRetain && doesntUpgradeRetain();
    }

    public static boolean doesntUpgradeRetain() {
        return baseCheck.selfRetain == upgradeCheck.selfRetain && baseCheck.selfRetain == branchingCheck.selfRetain;
    }

    public static boolean notReshuffle(AbstractCard card) {
        return !card.shuffleBackIntoDrawPile && doesntUpgradeReshuffle();
    }

    public static boolean doesntUpgradeReshuffle() {
        return baseCheck.shuffleBackIntoDrawPile == upgradeCheck.shuffleBackIntoDrawPile && baseCheck.shuffleBackIntoDrawPile == branchingCheck.shuffleBackIntoDrawPile;
    }

    public static boolean doesntUpgradeTargeting() {
        return baseCheck.target == upgradeCheck.target && baseCheck.target == branchingCheck.target;
    }

    public static boolean usesEnemyTargeting(AbstractCard card) {
        return targetsEnemy(card) && targetsEnemy(baseCheck) && targetsEnemy(upgradeCheck) && targetsEnemy(branchingCheck);
    }

    public static boolean canOverrideTargeting(AbstractCard card, AbstractCard.CardTarget desiredType) {
        if (desiredType == card.target || card.target == AbstractCard.CardTarget.NONE) {
            return true;
        }
        switch (desiredType) {
            case SELF_AND_ENEMY:
                return card.target == AbstractCard.CardTarget.ENEMY || card.target == AbstractCard.CardTarget.SELF;
            case ALL:
                return card.target == AbstractCard.CardTarget.ALL_ENEMY || card.target == AbstractCard.CardTarget.SELF;
        }
        return false;
    }

    public static boolean targetsEnemy(AbstractCard card) {
        return card.target == AbstractCard.CardTarget.ENEMY || card.target == AbstractCard.CardTarget.SELF_AND_ENEMY;
    }

    public static boolean usesVanillaTargeting(AbstractCard card) {
        return (card.target == AbstractCard.CardTarget.ENEMY
                || card.target == AbstractCard.CardTarget.ALL_ENEMY
                || card.target == AbstractCard.CardTarget.SELF
                || card.target == AbstractCard.CardTarget.NONE
                || card.target == AbstractCard.CardTarget.SELF_AND_ENEMY
                || card.target == AbstractCard.CardTarget.ALL);
    }

    private static boolean usesMagicBool;
    public static boolean usesMagic(AbstractCard card) {
        usesMagicBool = false;
        if (card.baseMagicNumber > 0 && StringUtils.containsIgnoreCase(card.rawDescription, "!M!") && !(card instanceof PanicButton) && !(card instanceof Halt)) {
            try {
                ClassPool pool = Loader.getClassPool();
                CtMethod ctClass = pool.get(card.getClass().getName()).getDeclaredMethod("use");

                ctClass.instrument(new ExprEditor() {
                    @Override
                    public void edit(FieldAccess f) {

                        if (f.getFieldName().equals("magicNumber") && !f.isWriter()) {
                            usesMagicBool = true;
                        }

                    }
                });

            } catch (Exception ignored) { }
        }
        return usesMagicBool;
    }

    public static boolean hasACurse() {
        return AbstractDungeon.player.masterDeck.group.stream().anyMatch(c -> c.type == AbstractCard.CardType.CURSE);
    }

    public static boolean allowOrbMods() {
        return CardAugmentsMod.allowOrbs || AbstractDungeon.player.hasRelic(PrismaticShard.ID) || CardAugmentsMod.ORB_CHARS.contains(AbstractDungeon.player.chosenClass);
    }

    public static boolean isNormalCard(AbstractCard card) {
        return card.type != AbstractCard.CardType.CURSE && card.type != AbstractCard.CardType.STATUS;
    }

    public static boolean doesntExhaust(AbstractCard card) {
        return !card.exhaust && !card.purgeOnUse && ExhaustiveField.ExhaustiveFields.baseExhaustive.get(card) == -1 && ExhaustiveField.ExhaustiveFields.exhaustive.get(card) == -1;
    }

    public static boolean doesntOverride(AbstractCard card, String method, Class<?>... paramtypez) {
        try {
            return card.getClass().getMethod(method, paramtypez).getDeclaringClass().equals(AbstractCard.class);
        } catch (NoSuchMethodException ignored) {}
        return false;
    }

    public static boolean noInterfaces(AbstractCard card) {
        try {
            ClassPool pool = Loader.getClassPool();
            CtClass ctClass = pool.get(card.getClass().getName());
            return ctClass.getInterfaces().length == 0;
        } catch (NotFoundException ignored) {}
        return false;
    }

    public static boolean noShenanigans(AbstractCard card) {
        return noInterfaces(card)
                && doesntOverride(card, "canUse", AbstractPlayer.class, AbstractMonster.class)
                && doesntOverride(card, "tookDamage")
                && doesntOverride(card, "didDiscard")
                && doesntOverride(card, "switchedStance")
                && doesntOverride(card, "triggerWhenDrawn")
                && doesntOverride(card, "triggerWhenCopied")
                && doesntOverride(card, "triggerOnEndOfTurnForPlayingCard")
                && doesntOverride(card, "triggerOnOtherCardPlayed", AbstractCard.class)
                && doesntOverride(card, "triggerOnGainEnergy", int.class, boolean.class)
                && doesntOverride(card, "switchedStance")
                && doesntOverride(card, "triggerOnManualDiscard")
                && doesntOverride(card, "triggerOnCardPlayed", AbstractCard.class)
                && doesntOverride(card, "triggerOnScry")
                && doesntOverride(card, "triggerExhaustedCardsOnStanceChange", AbstractStance.class)
                && doesntOverride(card, "triggerAtStartOfTurn")
                && doesntOverride(card, "onPlayCard", AbstractCard.class, AbstractMonster.class)
                && doesntOverride(card, "atTurnStart")
                && doesntOverride(card, "atTurnStartPreDraw")
                && doesntOverride(card, "onChoseThisOption")
                && doesntOverride(card, "onRetained")
                && doesntOverride(card, "triggerOnExhaust");
    }

    public static String[] removeUpgradeText(String name) {
        //Set up the return Strings
        String[] ret = new String[]{name, ""};
        //If it ends in a single + and *, remove it
        if (name.endsWith("+") || name.endsWith("*")) {
            ret[1] = name.substring(name.length()-1);
            ret[0] = name.substring(0, name.length()-1);
        } else {
            //See if it has a +
            int index = name.lastIndexOf("+");
            if (index == -1) { //Failing that try a *
                index = name.lastIndexOf("*");
            }
            //If everything after the + or * is a number, this is our upgrade text
            if (index != -1 && name.substring(index+1).chars().allMatch(Character::isDigit)) {
                ret[1] = name.substring(index);
                ret[0] = name.substring(0, index);
            }
        }
        return ret;
    }
}
