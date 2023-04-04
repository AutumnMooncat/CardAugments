package CardAugments.patches.events;

import CardAugments.CardAugmentsMod;
import CardAugments.cardmods.AbstractAugment;
import CardAugments.cardmods.event.EchoMod;
import CardAugments.util.AugmentPreviewCard;
import basemod.helpers.CardModifierManager;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractEvent;
import com.megacrit.cardcrawl.events.shrines.Duplicator;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.vfx.UpgradeShineEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardBrieflyEffect;
import javassist.CtBehavior;

public class DuplicatorPatches {
    private static final UIStrings STRINGS = CardCrawlGame.languagePack.getUIString(CardAugmentsMod.makeID("DuplicatorEvent"));
    public static final String[] TEXT = STRINGS.TEXT;
    public static final String[] OPTIONS = STRINGS.EXTRA_TEXT;
    public static int myIndex = -1;
    public static boolean choseMyOption = false;
    static AbstractAugment augment = null;
    @SpirePatch2(clz = Duplicator.class, method = SpirePatch.CONSTRUCTOR)
    public static class EventInit {
        @SpirePostfixPatch
        public static void addOption(Duplicator __instance) {
            if (CardAugmentsMod.eventAddons) {
                choseMyOption = false;
                augment = new EchoMod();
                //Rip the leave button out and put it back later
                __instance.imageEventText.clearRemainingOptions();
                myIndex = __instance.imageEventText.optionList.size();
                if (AbstractDungeon.player.masterDeck.group.stream().anyMatch(c -> augment.validCard(c))) {
                    __instance.imageEventText.setDialogOption(OPTIONS[0], new AugmentPreviewCard(TEXT[2], TEXT[3]));
                } else {
                    __instance.imageEventText.setDialogOption(OPTIONS[1], true);
                }
                __instance.imageEventText.setDialogOption(OPTIONS[2]);
            }
        }
    }

    @SpirePatch2(clz = Duplicator.class, method = "buttonEffect")
    public static class ButtonLogic {
        @SpirePrefixPatch
        public static SpireReturn<?> buttonPress(Duplicator __instance, @ByRef int[] buttonPressed, @ByRef int[] ___screenNum) {
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

    @SpirePatch2(clz = Duplicator.class, method = "update")
    public static class UpdateSnag {
        @SpireInsertPatch(locator = Locator.class)
        public static SpireReturn<?> update(Duplicator __instance) {
            if (CardAugmentsMod.eventAddons) {
                if (choseMyOption) {
                    if (!AbstractDungeon.isScreenUp && !AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
                        AbstractCard c = AbstractDungeon.gridSelectScreen.selectedCards.get(0);
                        CardModifierManager.addModifier(c, augment);
                        AbstractDungeon.effectsQueue.add(new ShowCardBrieflyEffect(c.makeStatEquivalentCopy()));
                        AbstractDungeon.topLevelEffects.add(new UpgradeShineEffect((float) Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F));
                        AbstractDungeon.gridSelectScreen.selectedCards.clear();
                        AbstractEvent.logMetricCardUpgrade("Duplicator", "Imbue", c);
                        choseMyOption = false;
                    }
                    return SpireReturn.Return();
                }
            }
            return SpireReturn.Continue();
        }

        public static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                Matcher m = new Matcher.FieldAccessMatcher(AbstractDungeon.class, "isScreenUp");
                return LineFinder.findInOrder(ctBehavior, m);
            }
        }
    }
}
