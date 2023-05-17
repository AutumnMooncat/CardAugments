package CardAugments.patches.events.downfall;

import CardAugments.CardAugmentsMod;
import CardAugments.cardmods.AbstractAugment;
import CardAugments.cardmods.event.ShiningMod;
import CardAugments.util.AugmentPreviewCard;
import CardAugments.util.Wiz;
import basemod.ReflectionHacks;
import basemod.helpers.CardModifierManager;
import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractEvent;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.vfx.UpgradeShineEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardBrieflyEffect;
import com.megacrit.cardcrawl.vfx.combat.FlashAtkImgEffect;
import downfall.events.ShiningLight_Evil;

import java.util.ArrayList;
import java.util.Collections;

public class ShiningEvilPatches {
    private static final UIStrings STRINGS = CardCrawlGame.languagePack.getUIString(CardAugmentsMod.makeID("ShiningEvent"));
    public static final String[] TEXT = STRINGS.TEXT;
    public static final String[] OPTIONS = STRINGS.EXTRA_TEXT;
    public static int myIndex = -1;
    public static boolean choseMyOption = false;
    public static AbstractAugment augment;
    public static int damage = 0;
    public static Object[] enumElements = null;

    @SpirePatch2(clz = ShiningLight_Evil.class, method = SpirePatch.CONSTRUCTOR, requiredModId = "downfall")
    public static class EventInit {
        @SpirePostfixPatch
        public static void addOption(ShiningLight_Evil __instance) {
            if (CardAugmentsMod.eventAddons) {
                choseMyOption = false;
                augment = new ShiningMod();
                //Rip the leave button out and put it back later
                __instance.imageEventText.clearRemainingOptions();
                myIndex = __instance.imageEventText.optionList.size();
                if (AbstractDungeon.player.masterDeck.group.stream().anyMatch(augment::canApplyTo)) {
                    if (AbstractDungeon.ascensionLevel >= 15) {
                        damage = MathUtils.round((float)AbstractDungeon.player.maxHealth * 0.15F);
                    } else {
                        damage = MathUtils.round((float)AbstractDungeon.player.maxHealth * 0.1F);
                    }
                    __instance.imageEventText.setDialogOption(String.format(OPTIONS[0], damage), new AugmentPreviewCard(TEXT[1], TEXT[2]));
                } else {
                    __instance.imageEventText.setDialogOption(OPTIONS[1], true);
                }
                __instance.imageEventText.setDialogOption(OPTIONS[2]);
                if (enumElements == null) {
                    try {
                        Class<?> enumElement = Class.forName(ShiningLight_Evil.class.getName()+"$CUR_SCREEN");
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

    @SpirePatch2(clz = ShiningLight_Evil.class, method = "buttonEffect", requiredModId = "downfall")
    public static class ButtonLogic {
        @SpirePrefixPatch
        public static SpireReturn<?> buttonPress(ShiningLight_Evil __instance, @ByRef int[] buttonPressed, Object ___screen) {
            if (CardAugmentsMod.eventAddons) {
                if (___screen == enumElements[0]) {
                    if (buttonPressed[0] == myIndex) {
                        AbstractDungeon.player.damage(new DamageInfo(null, damage, DamageInfo.DamageType.HP_LOSS));
                        AbstractDungeon.effectList.add(new FlashAtkImgEffect(AbstractDungeon.player.hb.cX, AbstractDungeon.player.hb.cY, AbstractGameAction.AttackEffect.FIRE));
                        __instance.imageEventText.clearRemainingOptions();
                        __instance.imageEventText.updateBodyText(TEXT[0]);
                        __instance.imageEventText.updateDialogOption(0, OPTIONS[2]);
                        ArrayList<AbstractCard> validCards = new ArrayList<>();
                        for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
                            if (augment.canApplyTo(c)) {
                                validCards.add(c);
                            }
                        }
                        AbstractCard c = Wiz.getRandomItem(validCards);
                        CardModifierManager.addModifier(c, augment);
                        AbstractDungeon.effectsQueue.add(new ShowCardBrieflyEffect(c.makeStatEquivalentCopy()));
                        AbstractDungeon.topLevelEffects.add(new UpgradeShineEffect((float) Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F));
                        AbstractDungeon.gridSelectScreen.selectedCards.clear();
                        AbstractEvent.logMetric("downfall:ShiningLight", "Throw", null, null, null, Collections.singletonList(c.cardID), null, null, null, damage, 0, 0, 0, 0, 0);
                        choseMyOption = true;
                        ReflectionHacks.setPrivate(__instance, ShiningLight_Evil.class, "screen", enumElements[1]);
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
