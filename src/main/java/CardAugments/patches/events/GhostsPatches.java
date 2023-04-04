package CardAugments.patches.events;

import CardAugments.CardAugmentsMod;
import CardAugments.cardmods.AbstractAugment;
import CardAugments.cardmods.event.AberrantMod;
import CardAugments.util.AugmentPreviewCard;
import basemod.helpers.CardModifierManager;
import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractEvent;
import com.megacrit.cardcrawl.events.city.Ghosts;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.vfx.UpgradeShineEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardBrieflyEffect;
import javassist.*;
import javassist.bytecode.DuplicateMemberException;

import java.util.Collections;

public class GhostsPatches {
    private static final UIStrings STRINGS = CardCrawlGame.languagePack.getUIString(CardAugmentsMod.makeID("GhostsEvent"));
    public static final String[] TEXT = STRINGS.TEXT;
    public static final String[] OPTIONS = STRINGS.EXTRA_TEXT;
    public static int myIndex = -1;
    public static boolean choseMyOption = false;
    static AbstractAugment augment = null;
    static int hpLoss;
    @SpirePatch2(clz = Ghosts.class, method = SpirePatch.CONSTRUCTOR)
    public static class EventInit {
        @SpirePostfixPatch
        public static void addOption(Ghosts __instance) {
            if (CardAugmentsMod.eventAddons) {
                choseMyOption = false;
                augment = new AberrantMod();
                hpLoss = MathUtils.ceil((float)AbstractDungeon.player.maxHealth * 0.125F);// 33
                if (hpLoss >= AbstractDungeon.player.maxHealth) {// 34
                    hpLoss = AbstractDungeon.player.maxHealth - 1;// 35
                }
                //Rip the leave button out and put it back later
                __instance.imageEventText.clearRemainingOptions();
                myIndex = __instance.imageEventText.optionList.size();
                if (AbstractDungeon.player.masterDeck.group.stream().anyMatch(c -> augment.validCard(c))) {
                    __instance.imageEventText.setDialogOption(String.format(OPTIONS[0], hpLoss), new AugmentPreviewCard(TEXT[2], TEXT[3]));
                } else {
                    __instance.imageEventText.setDialogOption(OPTIONS[1], true);
                }
                __instance.imageEventText.setDialogOption(OPTIONS[2]);
            }
        }
    }

    @SpirePatch2(clz = Ghosts.class, method = "buttonEffect")
    public static class ButtonLogic {
        @SpirePrefixPatch
        public static SpireReturn<?> buttonPress(Ghosts __instance, @ByRef int[] buttonPressed, @ByRef int[] ___screenNum) {
            if (CardAugmentsMod.eventAddons) {
                if (___screenNum[0] == 0) {
                    //If we click the new leave button, let it act as if we pressed the old leave button
                    if (buttonPressed[0] == myIndex + 1) {
                        buttonPressed[0] = 1;
                        return SpireReturn.Continue();
                    }
                    if (buttonPressed[0] == myIndex) {
                        __instance.imageEventText.clearRemainingOptions();
                        __instance.imageEventText.updateBodyText(TEXT[0]);
                        __instance.imageEventText.updateDialogOption(0, OPTIONS[2]);
                        CardGroup group = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
                        for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
                            if (augment.validCard(c)) {
                                group.addToTop(c);
                            }
                        }
                        AbstractDungeon.gridSelectScreen.open(group, 1, TEXT[1], false, false, false, false);
                        choseMyOption = true;
                        ___screenNum[0] = 2;
                        return SpireReturn.Return();
                    }
                }
            }
            return SpireReturn.Continue();
        }
    }

    public static void updateEvent(Ghosts __instance) {
        if (CardAugmentsMod.eventAddons) {
            if (choseMyOption && !AbstractDungeon.isScreenUp && !AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
                AbstractCard c = AbstractDungeon.gridSelectScreen.selectedCards.get(0);
                CardModifierManager.addModifier(c, augment);
                AbstractDungeon.effectsQueue.add(new ShowCardBrieflyEffect(c.makeStatEquivalentCopy()));
                AbstractDungeon.topLevelEffects.add(new UpgradeShineEffect((float) Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F));
                AbstractDungeon.gridSelectScreen.selectedCards.clear();
                choseMyOption = false;
                AbstractDungeon.player.decreaseMaxHealth(hpLoss);
                AbstractEvent.logMetric("Ghosts", "Imbue", null, null, null, Collections.singletonList(c.cardID), null, null, null, 0, 0, hpLoss, 0, 0, 0);
            }
        }
    }

    @SpirePatch2(clz = Ghosts.class, method = SpirePatch.CONSTRUCTOR)
    public static class AddUpdateOverride {
        @SpireRawPatch
        public static void addMethod(CtBehavior ctMethodToPatch) throws CannotCompileException, NotFoundException {
            CtClass ctClass = ctMethodToPatch.getDeclaringClass();
            CtClass superClass = ctClass.getSuperclass();
            CtMethod superMethod = superClass.getDeclaredMethod("update");
            CtMethod updateMethod = CtNewMethod.delegator(superMethod, ctClass);
            try {
                ctClass.addMethod(updateMethod);
            } catch (DuplicateMemberException ignored) {
                updateMethod = ctClass.getDeclaredMethod("update");
            }
            updateMethod.insertAfter(GhostsPatches.class.getName()+".updateEvent($0);");
        }
    }
}
