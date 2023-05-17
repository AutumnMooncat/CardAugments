package CardAugments.patches.events.downfall;

import CardAugments.CardAugmentsMod;
import CardAugments.cardmods.AbstractAugment;
import CardAugments.cardmods.rare.GreedMod;
import CardAugments.cardmods.rare.SluggerMod;
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
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.vfx.UpgradeShineEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardBrieflyEffect;
import downfall.events.Serpent_Evil;

import java.util.ArrayList;
import java.util.List;

public class SerpentEvilPatches {
    private static final UIStrings STRINGS = CardCrawlGame.languagePack.getUIString(CardAugmentsMod.makeID("SerpentEventDownfall"));
    public static final String[] TEXT = STRINGS.TEXT;
    public static final String[] OPTIONS = STRINGS.EXTRA_TEXT;
    public static int myIndex = -1;
    public static boolean choseMyOption = false;
    public static boolean needsContinue = false;
    public static AbstractAugment augment;
    public static Object[] enumElements = null;

    @SpirePatch2(clz = Serpent_Evil.class, method = SpirePatch.CONSTRUCTOR, requiredModId = "downfall")
    public static class EventInit {
        @SpirePostfixPatch
        public static void addOption(Serpent_Evil __instance) {
            if (CardAugmentsMod.eventAddons) {
                choseMyOption = false;
                needsContinue = false;
                augment = new SluggerMod();
                //Rip the leave button out and put it back later
                __instance.imageEventText.clearRemainingOptions();
                myIndex = __instance.imageEventText.optionList.size();
                if (AbstractDungeon.player.masterDeck.group.stream().anyMatch(augment::canApplyTo)) {
                    __instance.imageEventText.setDialogOption(String.format(OPTIONS[0]), new AugmentPreviewCard(TEXT[2], TEXT[3]));
                } else {
                    __instance.imageEventText.setDialogOption(OPTIONS[1], true);
                }
                __instance.imageEventText.setDialogOption(OPTIONS[2]);
                if (enumElements == null) {
                    try {
                        Class<?> enumElement = Class.forName(Serpent_Evil.class.getName()+"$CUR_SCREEN");
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

    @SpirePatch2(clz = Serpent_Evil.class, method = "buttonEffect", requiredModId = "downfall")
    public static class ButtonLogic {
        @SpirePrefixPatch
        public static SpireReturn<?> buttonPress(Serpent_Evil __instance, @ByRef int[] buttonPressed, Object ___screen) {
            if (CardAugmentsMod.eventAddons) {
                if (___screen == enumElements[0]) {
                    if (needsContinue) {
                        __instance.imageEventText.updateBodyText(TEXT[1]);
                        __instance.imageEventText.updateDialogOption(0, OPTIONS[2]);
                        ArrayList<AbstractCard> validCards = new ArrayList<>();
                        List<String> cardMetrics = new ArrayList<>();
                        for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
                            if (augment.canApplyTo(c)) {
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

                        AbstractEvent.logMetric("downfall:Serpent", "Agree", null, null, null, cardMetrics, null, null, null, 0, 0, 0, 0, 0, 0);
                        choseMyOption = true;
                        needsContinue = false;
                        ReflectionHacks.setPrivate(__instance, Serpent_Evil.class, "screen", enumElements[3]);
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
