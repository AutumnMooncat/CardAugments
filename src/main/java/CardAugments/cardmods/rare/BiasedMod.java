package CardAugments.cardmods.rare;

import CardAugments.CardAugmentsMod;
import CardAugments.cardmods.AbstractAugment;
import CardAugments.powers.BiasPower;
import basemod.ReflectionHacks;
import basemod.abstracts.AbstractCardModifier;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.DrawPower;
import com.megacrit.cardcrawl.powers.RepairPower;
import javassist.*;
import javassist.expr.ExprEditor;
import javassist.expr.NewExpr;

public class BiasedMod extends AbstractAugment {
    public static final String ID = CardAugmentsMod.makeID(BiasedMod.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;

    private static final ClassPool pool = Loader.getClassPool();

    private static final int MULTIPLIER = 3;

    private int baseAmount;

    @Override
    public void onInitialApplication(AbstractCard card) {
        baseAmount = card.baseMagicNumber;
        card.magicNumber = card.baseMagicNumber = baseAmount * MULTIPLIER;
    }

      /**
      * Checks if the card is a power that uses magic and applies only a single power with its use() function.
      * If more than one power is applied, or if any other action is taken (besides vfx or sfx actions) then the card
      * is disqualified.
      **/
    @Override
    public boolean validCard(AbstractCard card) {
        if (card.type == AbstractCard.CardType.POWER && usesMagic(card)) {
            try {
                CtClass ctClass = pool.get(card.getClass().getName());
                ctClass.defrost();
                CtMethod useMethod;
                useMethod = ctClass.getDeclaredMethod("use");

                boolean[] appliesOnePower = {false};
                boolean[] simple = {true};
                useMethod.instrument(new ExprEditor() {
                    @Override
                    public void edit(NewExpr e) {
                        try {
                            CtClass cls = e.getConstructor().getDeclaringClass();
                            if (cls.subclassOf(pool.get(AbstractGameAction.class.getName()))) {
                                if (cls.subclassOf(pool.get(ApplyPowerAction.class.getName()))) {
                                    if (appliesOnePower[0])
                                        simple[0] = false;
                                    appliesOnePower[0] = true;
                                } else if (
                                        !cls.subclassOf(pool.get(VFXAction.class.getName())) &&
                                        !cls.subclassOf(pool.get(SFXAction.class.getName()))
                                )
                                    simple[0] = false;
                            }
                            if (cls.subclassOf(pool.get(DrawPower.class.getName()))) {
                                // DrawPower is just implemented in a way that does not play nicely with being reduced
                                // over time.
                                simple[0] = false;
                            }
                        } catch (NotFoundException ex) {
                            ex.printStackTrace();
                        }
                    }
                });
                CardAugmentsMod.logger.info(card.name + " is biasable: " + simple[0]);
                return simple[0];
            } catch (NotFoundException | CannotCompileException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    @Override
    public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
        AbstractPower powerApplied = null;
        for (AbstractGameAction aga: AbstractDungeon.actionManager.actions)
            if (aga instanceof ApplyPowerAction) {
                powerApplied = ReflectionHacks.getPrivate(aga, ApplyPowerAction.class, "powerToApply");
                break;
            }
        if (powerApplied == null)
            return;

        // Set the power to be able to go negative, even if it wasn't designed to. This results in a lot of very
        // buggy-feeling but mostly harmless and very funny interactions. An unfortunate exception is Self-Repair, which
        // when Biased can and will set HP to negative, softlocking the game at the start of the next combat. While this
        // could be considered an alternative lose condition, it can also be frustrating to get the player's hopes
        // up only to crush them a room, or several, later. Uncommenting this line should be considered, or even
        // blacklisting Self-Repair from the mod entirely.
        // Alternatively, one could patch RepairPower to function properly when negative.
//        if (!(powerApplied instanceof RepairPower))
            powerApplied.canGoNegative = true;

        addToBot(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player,
                new BiasPower(AbstractDungeon.player, baseAmount, powerApplied), baseAmount));
    }

    @Override
    public String modifyName(String cardName, AbstractCard card) {
        return TEXT[0] + cardName + TEXT[1];
    }

    @Override
    public String modifyDescription(String rawDescription, AbstractCard card) {
        return rawDescription + String.format(TEXT[2], baseAmount);
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.RARE;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new BiasedMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}
