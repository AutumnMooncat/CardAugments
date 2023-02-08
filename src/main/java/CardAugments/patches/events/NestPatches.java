package CardAugments.patches.events;

import CardAugments.CardAugmentsMod;
import CardAugments.cardmods.AbstractAugment;
import CardAugments.cardmods.event.CultistMod;
import CardAugments.cardmods.event.FanaticMod;
import basemod.helpers.CardModifierManager;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractEvent;
import com.megacrit.cardcrawl.events.city.Nest;
import com.megacrit.cardcrawl.vfx.UpgradeShineEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardBrieflyEffect;
import javassist.*;
import javassist.bytecode.DuplicateMemberException;

public class NestPatches {
    public static final String[] NEST_TEXT = CardCrawlGame.languagePack.getUIString(CardAugmentsMod.makeID("NestEvent")).TEXT;
    public static int observeIndex = -1;
    public static boolean choseObserve = false;
    public static boolean pickedCard = false;
    public static AbstractAugment augment = null;
    @SpirePatch2(clz = Nest.class, method = SpirePatch.CONSTRUCTOR)
    public static class NestInit {
        @SpirePostfixPatch
        public static void addOption(Nest __instance) {
            choseObserve = false;
            pickedCard = false;
            augment = null;
            observeIndex = __instance.imageEventText.optionList.size();
            if (AbstractDungeon.player.masterDeck.group.stream().anyMatch(c -> c.type == AbstractCard.CardType.ATTACK)) {
                __instance.imageEventText.setDialogOption(NEST_TEXT[0]);
            } else {
                __instance.imageEventText.setDialogOption(NEST_TEXT[6], false);
            }
        }
    }

    @SpirePatch2(clz = Nest.class, method = "buttonEffect")
    public static class ButtonLogic {
        @SpirePrefixPatch
        public static SpireReturn<?> buttonPress(Nest __instance, int buttonPressed, @ByRef int[] ___screenNum) {
            if (choseObserve) {
                pickedCard = true;
                __instance.imageEventText.updateBodyText(NEST_TEXT[5]);
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
                    if (augment.validCard(c)) {
                        group.addToBottom(c);
                    }
                }
                AbstractDungeon.gridSelectScreen.open(group, 1, NEST_TEXT[7], false, false, false, false);
                ___screenNum[0] = 2;
                choseObserve = false;
                __instance.imageEventText.updateDialogOption(0, NEST_TEXT[3]);
                __instance.imageEventText.clearRemainingOptions();
                return SpireReturn.Return();
            } else if (___screenNum[0] == 0) {
                if (buttonPressed == observeIndex) {
                    __instance.imageEventText.clearRemainingOptions();
                    __instance.imageEventText.updateBodyText(NEST_TEXT[4]);
                    __instance.imageEventText.updateDialogOption(0, NEST_TEXT[1]);
                    __instance.imageEventText.setDialogOption(NEST_TEXT[2]);
                    choseObserve = true;
                    return SpireReturn.Return();
                } else {
                    __instance.imageEventText.removeDialogOption(observeIndex);
                }
            }
            return SpireReturn.Continue();
        }
    }

    public static void updateNest(Nest __instance) {
        if (pickedCard && !AbstractDungeon.isScreenUp && !AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
            AbstractCard c = AbstractDungeon.gridSelectScreen.selectedCards.get(0);
            CardModifierManager.addModifier(c, augment);
            AbstractDungeon.effectsQueue.add(new ShowCardBrieflyEffect(c.makeStatEquivalentCopy()));
            AbstractDungeon.topLevelEffects.add(new UpgradeShineEffect((float) Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F));
            AbstractDungeon.gridSelectScreen.selectedCards.clear();
            pickedCard = false;
            AbstractEvent.logMetricCardUpgrade("Nest", "Observe", c);
        }
    }

    @SpirePatch2(clz = Nest.class, method = SpirePatch.CONSTRUCTOR)
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
            updateMethod.insertAfter(NestPatches.class.getName()+".updateNest($0);");
        }
    }
}
