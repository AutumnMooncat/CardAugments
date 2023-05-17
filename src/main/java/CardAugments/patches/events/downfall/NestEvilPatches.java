package CardAugments.patches.events.downfall;

import CardAugments.CardAugmentsMod;
import CardAugments.cardmods.AbstractAugment;
import CardAugments.cardmods.event.CultistMod;
import CardAugments.cardmods.event.FanaticMod;
import CardAugments.util.AugmentPreviewCard;
import basemod.ReflectionHacks;
import basemod.helpers.CardModifierManager;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractEvent;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.vfx.UpgradeShineEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardBrieflyEffect;
import downfall.events.TheNest_Evil;
import javassist.*;
import javassist.bytecode.DuplicateMemberException;

public class NestEvilPatches {
    private static final UIStrings STRINGS = CardCrawlGame.languagePack.getUIString(CardAugmentsMod.makeID("NestEventDownfall"));
    public static final String[] TEXT = STRINGS.TEXT;
    public static final String[] OPTIONS = STRINGS.EXTRA_TEXT;
    public static int observeIndex = -1;
    public static boolean choseObserve = false;
    public static boolean pickedCard = false;
    public static AbstractAugment augment = null;
    public static Object[] enumElements = null;
    @SpirePatch2(clz = TheNest_Evil.class, method = SpirePatch.CONSTRUCTOR, requiredModId = "downfall")
    public static class NestInit {
        @SpirePostfixPatch
        public static void addOption(TheNest_Evil __instance) {
            if (CardAugmentsMod.eventAddons) {
                choseObserve = false;
                pickedCard = false;
                augment = null;
                observeIndex = __instance.imageEventText.optionList.size();
                if (AbstractDungeon.player.masterDeck.group.stream().anyMatch(c -> c.type == AbstractCard.CardType.ATTACK)) {
                    __instance.imageEventText.setDialogOption(OPTIONS[0]);
                } else {
                    __instance.imageEventText.setDialogOption(OPTIONS[1], true);
                }
                if (enumElements == null) {
                    try {
                        Class<?> enumElement = Class.forName(TheNest_Evil.class.getName()+"$CUR_SCREEN");
                        if (enumElement.isEnum()) {
                            enumElements = enumElement.getEnumConstants();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @SpirePatch2(clz = TheNest_Evil.class, method = "buttonEffect", requiredModId = "downfall")
    public static class ButtonLogic {
        @SpirePrefixPatch
        public static SpireReturn<?> buttonPress(TheNest_Evil __instance, int buttonPressed, Object ___screen) {
            if (CardAugmentsMod.eventAddons) {
                if (choseObserve) {
                    pickedCard = true;
                    __instance.imageEventText.updateBodyText(TEXT[1]);
                    switch (buttonPressed) {
                        case 0:
                            augment = new CultistMod();
                            break;
                        case 1:
                            augment = new FanaticMod();
                            break;
                    }
                    CardGroup group = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
                    for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
                        if (augment.canApplyTo(c)) {
                            group.addToTop(c);
                        }
                    }
                    AbstractDungeon.gridSelectScreen.open(group, 1, TEXT[2], false, false, false, false);
                    ReflectionHacks.setPrivate(__instance, TheNest_Evil.class, "screen", enumElements[5]);
                    choseObserve = false;
                    __instance.imageEventText.updateDialogOption(0, OPTIONS[5]);
                    __instance.imageEventText.clearRemainingOptions();
                    return SpireReturn.Return();
                } else if (___screen == enumElements[0]) {
                    if (buttonPressed == observeIndex) {
                        __instance.imageEventText.clearRemainingOptions();
                        __instance.imageEventText.updateBodyText(TEXT[0]);
                        __instance.imageEventText.updateDialogOption(0, OPTIONS[2], new AugmentPreviewCard(TEXT[3], TEXT[4]));
                        FanaticMod mod = new FanaticMod();
                        if (AbstractDungeon.player.masterDeck.group.stream().anyMatch(mod::canApplyTo)) {
                            __instance.imageEventText.setDialogOption(OPTIONS[3], new AugmentPreviewCard(TEXT[5], TEXT[6]));
                        } else {
                            __instance.imageEventText.setDialogOption(OPTIONS[4], true);
                        }
                        choseObserve = true;
                        return SpireReturn.Return();
                    } else {
                        __instance.imageEventText.removeDialogOption(observeIndex);
                    }
                }
            }
            return SpireReturn.Continue();
        }
    }

    public static void updateNest(TheNest_Evil __instance) {
        if (CardAugmentsMod.eventAddons) {
            if (pickedCard && !AbstractDungeon.isScreenUp && !AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
                AbstractCard c = AbstractDungeon.gridSelectScreen.selectedCards.get(0);
                CardModifierManager.addModifier(c, augment);
                AbstractDungeon.effectsQueue.add(new ShowCardBrieflyEffect(c.makeStatEquivalentCopy()));
                AbstractDungeon.topLevelEffects.add(new UpgradeShineEffect((float) Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F));
                AbstractDungeon.gridSelectScreen.selectedCards.clear();
                pickedCard = false;
                AbstractEvent.logMetricCardUpgrade("downfall:Nest", "Observe", c);
            }
        }
    }

    @SpirePatch2(clz = TheNest_Evil.class, method = SpirePatch.CONSTRUCTOR, requiredModId = "downfall")
    public static class AddUpdateOverride {
        @SpireRawPatch
        public static void addMethod(CtBehavior ctMethodToPatch) throws CannotCompileException, NotFoundException {
            CtClass ctNestClass = ctMethodToPatch.getDeclaringClass();
            CtClass superClass = ctNestClass.getSuperclass();
            CtMethod superMethod = superClass.getDeclaredMethod("update");
            CtMethod updateMethod = CtNewMethod.delegator(superMethod, ctNestClass);
            try {
                ctNestClass.addMethod(updateMethod);
            } catch (DuplicateMemberException ignored) {
                updateMethod = ctNestClass.getDeclaredMethod("update");
            }
            updateMethod.insertAfter(NestEvilPatches.class.getName()+".updateNest($0);");
        }
    }
}
