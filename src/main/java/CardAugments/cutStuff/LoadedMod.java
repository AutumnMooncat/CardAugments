package CardAugments.cutStuff;

import CardAugments.CardAugmentsMod;
import CardAugments.cardmods.AbstractAugment;
import CardAugments.util.PortraitHelper;
import basemod.ReflectionHacks;
import basemod.abstracts.AbstractCardModifier;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.blue.BiasedCognition;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import javassist.*;
import javassist.expr.ExprEditor;
import javassist.expr.NewExpr;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class LoadedMod extends AbstractAugment {
    public static final String ID = CardAugmentsMod.makeID(LoadedMod.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;

    private static final ClassPool pool = Loader.getClassPool();

    private static final String[] startAndEndTurnMethods = new String[]{
            "atStartOfTurn",
            "atStartOfTurnPostDraw",
            "atEndOfTurn",
            "atEndOfTurnPreEndTurnCards",
            "atEndOfRound"
    };

    private static final int MULTIPLIER = 4;

    @Override
    public void onInitialApplication(AbstractCard card) {
        card.type = AbstractCard.CardType.SKILL;
        card.exhaust = true;
        PortraitHelper.setMaskedPortrait(card);
    }

    @Override
    public boolean validCard(AbstractCard card) {
        return card.type == AbstractCard.CardType.POWER && !(card instanceof BiasedCognition) && hasStartAndEndTurnMethods(card);
    }

    private static boolean hasStartAndEndTurnMethods(AbstractCard card) {
        final boolean[] retVal = {false};
        if (card.type != AbstractCard.CardType.POWER)
            return false;
        try {
            CtClass ctClass = pool.get(card.getClass().getName());
            ctClass.defrost();
            CtMethod useMethod;
            useMethod = ctClass.getDeclaredMethod("use");

            useMethod.instrument(new ExprEditor() {
                @Override
                public void edit(NewExpr e) {
                    try {
                        CtConstructor struct = e.getConstructor();
                        CtClass cls = struct.getDeclaringClass();
                        if (validPower(cls))
                            retVal[0] = true;
                    } catch (NotFoundException ex) {
                        ex.printStackTrace();
                    }
                }
            });
        } catch (NotFoundException | CannotCompileException e) {
            e.printStackTrace();
        }
        CardAugmentsMod.logger.info(card.name + " is biasable: " + retVal[0]);
        return retVal[0];
    }

    private static boolean validPower(CtClass pow) {
        boolean[] retVal = {false};
        /***********************************************************************************************
         This block checks all start/end turn methods, and checks to see if they add an action other
         than reducepower (making an assumption that the power would be reducing itself) or
         removepower (making the same assumption). As far as I know, this filters out all basegame
         powers that "dont do anything each turn" such as Vulnerable and Intangible, and should
         be pretty decent with mod compatibility.
         Alternative options to this filtering include:
         -Checking to see if the constructor sets isTurnBased to true. I can't figure out how to
         do this with javassist.
         -Filtering against a manual blacklist. Obviously borks mod compatibility.
         **********************************************************************************************/
        pow.defrost();
        for (String method: startAndEndTurnMethods) {
            try {
                pow.getDeclaredMethod(method).instrument(new ExprEditor() {
                    @Override
                    public void edit(NewExpr e) throws CannotCompileException {
                        try {
                            CtClass newClass = e.getConstructor().getDeclaringClass();
                            if (
                                    newClass.subclassOf(pool.get(AbstractGameAction.class.getName())) &&
                                            !newClass.subclassOf(pool.get(ReducePowerAction.class.getName())) &&
                                            !newClass.subclassOf(pool.get(RemoveSpecificPowerAction.class.getName()))
                            )
                                retVal[0] = true;
                        } catch (NotFoundException ignored) {}
                    }
                });
            } catch (NotFoundException | CannotCompileException ignored) {}
        }
        return retVal[0];
    }

    @Override
    public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
        // dare ye not seek the unholy magicks within
        Iterator<AbstractGameAction> actionIterator = AbstractDungeon.actionManager.actions.iterator();
        Map<AbstractPower, Method> invokes = new LinkedHashMap<>();
        Map<AbstractPower, Method> invokesWithBoolean = new LinkedHashMap<>();
        while (actionIterator.hasNext()) {
            AbstractGameAction aga = actionIterator.next();
            if (aga instanceof ApplyPowerAction) {
                AbstractPower pow = ReflectionHacks.getPrivate(aga, ApplyPowerAction.class, "powerToApply");
                try {
                    if (validPower(pool.get(pow.getClass().getName()))) {
                        boolean loaded = false;
                        for (String method : startAndEndTurnMethods) {
                            try {
                                if (method.equals(startAndEndTurnMethods[2]) || method.equals(startAndEndTurnMethods[3]))
                                    invokesWithBoolean.put(pow, pow.getClass().getDeclaredMethod(method, boolean.class));
                                else
                                    invokes.put(pow, pow.getClass().getDeclaredMethod(method));
                                loaded = true;
                            } catch (NoSuchMethodException ignored) {
                            }
                        }
                        if (loaded)
                            actionIterator.remove();
                    }
                } catch (NotFoundException ignored) {}
            }
        }
        for (AbstractPower pow: invokes.keySet())
            try {
                for (int i = 0; i < 4; i++)
                    invokes.get(pow).invoke(pow);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        for (AbstractPower pow: invokesWithBoolean.keySet())
            try {
                for (int i = 0; i < 4; i++)
                    invokesWithBoolean.get(pow).invoke(pow, true);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
    }

    @Override
    public String modifyName(String cardName, AbstractCard card) {
        return TEXT[0] + cardName + TEXT[1];
    }

    @Override
    public String modifyDescription(String rawDescription, AbstractCard card) {
        return rawDescription
                // replace all variants of "At the start of your turn" etc with "4 times"
                .replace(TEXT[4], String.format(TEXT[3], MULTIPLIER))
                .replace(TEXT[5], String.format(TEXT[3], MULTIPLIER))
                .replace(TEXT[6], String.format(TEXT[3], MULTIPLIER))
                .replace(TEXT[7], String.format(TEXT[3], MULTIPLIER))
                .replace(TEXT[9], String.format(TEXT[8], MULTIPLIER))
                .replace(TEXT[10], String.format(TEXT[8], MULTIPLIER))
                + TEXT[2];
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.UNCOMMON;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new LoadedMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}
