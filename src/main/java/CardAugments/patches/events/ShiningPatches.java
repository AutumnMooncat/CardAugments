package CardAugments.patches.events;

import CardAugments.CardAugmentsMod;
import CardAugments.cardmods.AbstractAugment;
import CardAugments.cardmods.event.ShiningMod;
import CardAugments.util.AugmentPreviewCard;
import CardAugments.util.Wiz;
import basemod.ReflectionHacks;
import basemod.helpers.CardModifierManager;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractEvent;
import com.megacrit.cardcrawl.events.exordium.ShiningLight;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.vfx.UpgradeShineEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardBrieflyEffect;
import com.megacrit.cardcrawl.vfx.combat.FlashAtkImgEffect;

import java.util.ArrayList;
import java.util.Collections;

public class ShiningPatches {
    private static final UIStrings STRINGS = CardCrawlGame.languagePack.getUIString(CardAugmentsMod.makeID("ShiningEvent"));
    public static final String[] TEXT = STRINGS.TEXT;
    public static final String[] OPTIONS = STRINGS.EXTRA_TEXT;
    public static int myIndex = -1;
    public static boolean choseMyOption = false;
    public static AbstractAugment augment;
    public static int damage = 0;

    @SpirePatch2(clz = ShiningLight.class, method = SpirePatch.CONSTRUCTOR)
    public static class EventInit {
        @SpirePostfixPatch
        public static void addOption(ShiningLight __instance) {
            if (CardAugmentsMod.eventAddons) {
                choseMyOption = false;
                augment = new ShiningMod();
                //Rip the leave button out and put it back later
                __instance.imageEventText.clearRemainingOptions();
                myIndex = __instance.imageEventText.optionList.size();
                if (AbstractDungeon.player.masterDeck.group.stream().anyMatch(augment::validCard)) {
                    damage = (int) (ReflectionHacks.<Integer>getPrivate(__instance, ShiningLight.class, "damage") * 0.5f);
                    __instance.imageEventText.setDialogOption(String.format(OPTIONS[0], damage), new AugmentPreviewCard(TEXT[1], TEXT[2]));
                } else {
                    __instance.imageEventText.setDialogOption(OPTIONS[1], true);
                }
                __instance.imageEventText.setDialogOption(OPTIONS[2]);
            }
        }
    }

    @SpirePatch2(clz = ShiningLight.class, method = "buttonEffect")
    public static class ButtonLogic {
        @SpirePrefixPatch
        public static SpireReturn<?> buttonPress(ShiningLight __instance, @ByRef int[] buttonPressed, @ByRef int[] ___screenNum) {
            if (CardAugmentsMod.eventAddons) {
                if (___screenNum[0] == 0) {
                    if (buttonPressed[0] == myIndex) {
                        AbstractDungeon.player.damage(new DamageInfo(AbstractDungeon.player, damage));
                        AbstractDungeon.effectList.add(new FlashAtkImgEffect(AbstractDungeon.player.hb.cX, AbstractDungeon.player.hb.cY, AbstractGameAction.AttackEffect.FIRE));
                        __instance.imageEventText.clearRemainingOptions();
                        __instance.imageEventText.updateBodyText(TEXT[0]);
                        __instance.imageEventText.updateDialogOption(0, OPTIONS[2]);
                        ArrayList<AbstractCard> validCards = new ArrayList<>();
                        for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
                            if (augment.validCard(c)) {
                                validCards.add(c);
                            }
                        }
                        AbstractCard c = Wiz.getRandomItem(validCards);
                        CardModifierManager.addModifier(c, augment);
                        AbstractDungeon.effectsQueue.add(new ShowCardBrieflyEffect(c.makeStatEquivalentCopy()));
                        AbstractDungeon.topLevelEffects.add(new UpgradeShineEffect((float) Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F));
                        AbstractDungeon.gridSelectScreen.selectedCards.clear();
                        AbstractEvent.logMetric("Shining Light", "Throw", null, null, null, Collections.singletonList(c.cardID), null, null, null, damage, 0, 0, 0, 0, 0);
                        choseMyOption = true;
                        try {
                            Class<?> enumElement = Class.forName(ShiningLight.class.getName()+"$CUR_SCREEN");
                            if (enumElement.isEnum()) {
                                Object[] enumElements = enumElement.getEnumConstants();
                                ReflectionHacks.setPrivate(__instance, ShiningLight.class, "screen", enumElements[1]);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return SpireReturn.Return();
                    } else {
                        __instance.imageEventText.removeDialogOption(myIndex+1);
                    }
                    //If we click the new leave button, let it act as if we pressed the old leave button
                    if (buttonPressed[0] == myIndex + 1) {
                        buttonPressed[0] = 1;
                        return SpireReturn.Continue();
                    }
                }
            }
            return SpireReturn.Continue();
        }
    }
}
