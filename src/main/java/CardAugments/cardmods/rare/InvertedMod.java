package CardAugments.cardmods.rare;

import CardAugments.CardAugmentsMod;
import CardAugments.cardmods.AbstractAugment;
import CardAugments.patches.ActionReplacementPatches;
import CardAugments.util.PortraitHelper;
import basemod.abstracts.AbstractCardModifier;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.common.ModifyBlockAction;
import com.megacrit.cardcrawl.actions.common.ModifyDamageAction;
import com.megacrit.cardcrawl.actions.defect.IncreaseMiscAction;
import com.megacrit.cardcrawl.actions.unique.RitualDaggerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.purple.Brilliance;
import com.megacrit.cardcrawl.cards.red.PerfectedStrike;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.CodeIterator;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.Opcode;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;
import javassist.expr.NewExpr;

import java.util.ArrayList;
import java.util.Arrays;

public class InvertedMod extends AbstractAugment {
    public static final String ID = CardAugmentsMod.makeID(InvertedMod.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;
    public static final ArrayList<Class<?>> bannedCards = new ArrayList<>(Arrays.asList(PerfectedStrike.class, Brilliance.class));
    private boolean toBlock;

    @Override
    public void onInitialApplication(AbstractCard card) {
        if (card.rawDescription.contains(TEXT[3]) && card.type != AbstractCard.CardType.SKILL) {
            card.type = AbstractCard.CardType.SKILL;
            PortraitHelper.setMaskedPortrait(card);
            card.baseBlock = card.baseDamage;
            toBlock = true;
        } else if (card.rawDescription.contains(TEXT[2]) && card.type != AbstractCard.CardType.ATTACK) {
            card.type = AbstractCard.CardType.ATTACK;
            PortraitHelper.setMaskedPortrait(card);
            card.baseDamage = card.baseBlock;
            card.target = AbstractCard.CardTarget.ENEMY;
            toBlock = false;
        }
        ActionReplacementPatches.InvertedFields.isInverted.set(card, true);
        ActionReplacementPatches.InvertedFields.toBlock.set(card, toBlock);

        final int[] hits = {0};
        ClassPool pool = Loader.getClassPool();
        try {
            CtClass ctClass = pool.getCtClass(card.getClass().getName());
            ctClass.defrost();
            MethodInfo info = ctClass.getClassFile2().getMethod("use");
            CodeAttribute ca = info.getCodeAttribute();
            CodeIterator it = ca.iterator();
            while (it.hasNext()) {
                int index = it.next();
                int op = it.byteAt(index);
                //This is naive and assumes the monster param is always loaded into index 2
                //This also picks up calls via VFX
                if (op == Opcode.ALOAD_2) {
                    hits[0]++;
                }
            }
            CtMethod ctUse = ctClass.getDeclaredMethod("use");
            ctUse.instrument(new ExprEditor(){
                @Override
                public void edit(NewExpr e) {
                    if (e.getClassName().equals(DamageAction.class.getName())) {
                        //This is very bad and can easily fail if the card has a self damage action
                        hits[0]--;
                    }
                }

                @Override
                public void edit(MethodCall m) {
                    try {
                        CtMethod check = m.getMethod();
                        check.instrument(new ExprEditor() {
                            @Override
                            public void edit(NewExpr e) {
                                if (e.getClassName().equals(DamageAction.class.getName())) {
                                    hits[0]--;
                                }
                            }
                        });
                    } catch (Exception ignored) {}
                }
            });

        } catch (Exception ignored) {}
        if (toBlock) {
            if (hits[0] > 0) {
                card.target = AbstractCard.CardTarget.SELF_AND_ENEMY;
            } else {
                card.target = AbstractCard.CardTarget.SELF;
            }
        }
    }

    @Override
    public void onUpgradeCheck(AbstractCard card) {
        if (toBlock) {
            card.baseBlock = card.baseDamage;
            card.upgradedBlock = card.upgradedDamage;
        } else {
            card.baseDamage = card.baseBlock;
            card.upgradedDamage = card.upgradedBlock;
        }
    }

    @Override
    public boolean validCard(AbstractCard card) {
        boolean damageText = cardCheck(card, c -> c.rawDescription.contains(TEXT[3]));
        boolean blockText = cardCheck(card, c -> c.rawDescription.contains(TEXT[2]));
        if (!damageText && !blockText) {
            return false;
        }
        boolean hasDamage = usesAction(card, DamageAction.class);
        boolean hasBlock = usesAction(card, GainBlockAction.class);
        if (hasDamage && hasBlock) {
            return false;
        }
        if (!hasDamage && !hasBlock) {
            return false;
        }
        return ((damageText && hasDamage) ^ (blockText && hasBlock)) && noShenanigans(card) && bannedCards.stream().noneMatch(c -> c.equals(card.getClass())) &&
                cardCheck(card, c -> customCheck(AbstractAugment::usesVanillaTargeting)) &&
                !usesAction(card, ModifyBlockAction.class) && !usesAction(card, ModifyDamageAction.class) && !usesAction(card, RitualDaggerAction.class) && !usesAction(card, IncreaseMiscAction.class);
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
    public String modifyDescription(String rawDescription, AbstractCard card) {
        if (rawDescription.contains(TEXT[3])) {
            return rawDescription.replace(TEXT[3], TEXT[2]);
        }
        if (rawDescription.contains(TEXT[2])) {
            return rawDescription.replace(TEXT[2], TEXT[3]);
        }
        return rawDescription + " NL ???";
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.RARE;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new InvertedMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}
