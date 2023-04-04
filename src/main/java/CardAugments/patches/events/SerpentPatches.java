package CardAugments.patches.events;

import CardAugments.CardAugmentsMod;
import CardAugments.cardmods.AbstractAugment;
import CardAugments.cardmods.rare.GreedMod;
import CardAugments.util.AugmentPreviewCard;
import basemod.ReflectionHacks;
import basemod.helpers.CardModifierManager;
import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractEvent;
import com.megacrit.cardcrawl.events.exordium.Sssserpent;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.vfx.UpgradeShineEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardBrieflyEffect;

import java.util.ArrayList;
import java.util.List;

public class SerpentPatches {
    private static final UIStrings STRINGS = CardCrawlGame.languagePack.getUIString(CardAugmentsMod.makeID("SerpentEvent"));
    public static final String[] TEXT = STRINGS.TEXT;
    public static final String[] OPTIONS = STRINGS.EXTRA_TEXT;
    public static int myIndex = -1;
    public static boolean choseMyOption = false;
    public static boolean needsContinue = false;
    public static AbstractAugment augment;
    public static int damage = 0;

    @SpirePatch2(clz = Sssserpent.class, method = SpirePatch.CONSTRUCTOR)
    public static class EventInit {
        @SpirePostfixPatch
        public static void addOption(Sssserpent __instance) {
            if (CardAugmentsMod.eventAddons) {
                choseMyOption = false;
                needsContinue = false;
                augment = new GreedMod();
                //Rip the leave button out and put it back later
                __instance.imageEventText.clearRemainingOptions();
                myIndex = __instance.imageEventText.optionList.size();
                if (AbstractDungeon.player.masterDeck.group.stream().anyMatch(augment::validCard)) {
                    __instance.imageEventText.setDialogOption(String.format(OPTIONS[0]), new AugmentPreviewCard(TEXT[2], TEXT[3]));
                } else {
                    __instance.imageEventText.setDialogOption(OPTIONS[1], true);
                }
                __instance.imageEventText.setDialogOption(OPTIONS[2]);
            }
        }
    }

    @SpirePatch2(clz = Sssserpent.class, method = "buttonEffect")
    public static class ButtonLogic {
        @SpirePrefixPatch
        public static SpireReturn<?> buttonPress(Sssserpent __instance, @ByRef int[] buttonPressed, @ByRef int[] ___screenNum) {
            if (CardAugmentsMod.eventAddons) {
                if (___screenNum[0] == 0) {
                    if (needsContinue) {
                        __instance.imageEventText.updateBodyText(TEXT[1]);
                        __instance.imageEventText.updateDialogOption(0, OPTIONS[2]);
                        ArrayList<AbstractCard> validCards = new ArrayList<>();
                        List<String> cardMetrics = new ArrayList<>();
                        for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
                            if (augment.validCard(c)) {
                                cardMetrics.add(c.cardID);
                                validCards.add(c);
                                CardModifierManager.addModifier(c, augment.makeCopy());
                                AbstractDungeon.effectList.add(new ShowCardBrieflyEffect(c.makeStatEquivalentCopy(), MathUtils.random(0.1F, 0.9F) * (float)Settings.WIDTH, MathUtils.random(0.2F, 0.8F) * (float)Settings.HEIGHT));
                            }
                        }
                        if (validCards.size() > 3) {
                            AbstractDungeon.topLevelEffects.add(new UpgradeShineEffect((float) Settings.WIDTH * 1/5F, (float)Settings.HEIGHT / 2.0F));
                            AbstractDungeon.topLevelEffects.add(new UpgradeShineEffect((float) Settings.WIDTH * 2/5F, (float)Settings.HEIGHT / 2.0F));
                            AbstractDungeon.topLevelEffects.add(new UpgradeShineEffect((float) Settings.WIDTH * 3/5F, (float)Settings.HEIGHT / 2.0F));
                            AbstractDungeon.topLevelEffects.add(new UpgradeShineEffect((float) Settings.WIDTH * 4/5F, (float)Settings.HEIGHT / 2.0F));
                        } else if (validCards.size() == 3) {
                            AbstractDungeon.topLevelEffects.add(new UpgradeShineEffect((float) Settings.WIDTH * 1/4F, (float)Settings.HEIGHT / 2.0F));
                            AbstractDungeon.topLevelEffects.add(new UpgradeShineEffect((float) Settings.WIDTH * 1/2F, (float)Settings.HEIGHT / 2.0F));
                            AbstractDungeon.topLevelEffects.add(new UpgradeShineEffect((float) Settings.WIDTH * 3/4F, (float)Settings.HEIGHT / 2.0F));
                        } else if (validCards.size() == 2) {
                            AbstractDungeon.topLevelEffects.add(new UpgradeShineEffect((float) Settings.WIDTH * 1/3F, (float)Settings.HEIGHT / 2.0F));
                            AbstractDungeon.topLevelEffects.add(new UpgradeShineEffect((float) Settings.WIDTH * 2/3F, (float)Settings.HEIGHT / 2.0F));
                        } else {
                            AbstractDungeon.topLevelEffects.add(new UpgradeShineEffect((float) Settings.WIDTH * 1/2F, (float)Settings.HEIGHT / 2.0F));
                        }

                        AbstractEvent.logMetric("Liars Game", "Agree", null, null, null, cardMetrics, null, null, null, 0, 0, 0, 0, 0, 0);
                        choseMyOption = true;
                        try {
                            Class<?> enumElement = Class.forName(Sssserpent.class.getName()+"$CUR_SCREEN");
                            if (enumElement.isEnum()) {
                                Object[] enumElements = enumElement.getEnumConstants();
                                ReflectionHacks.setPrivate(__instance, Sssserpent.class, "screen", enumElements[3]);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return SpireReturn.Return();
                    }
                    if (buttonPressed[0] == myIndex) {
                        __instance.imageEventText.clearRemainingOptions();
                        __instance.imageEventText.updateBodyText(TEXT[0]);
                        __instance.imageEventText.updateDialogOption(0, OPTIONS[3]);
                        needsContinue = true;
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
