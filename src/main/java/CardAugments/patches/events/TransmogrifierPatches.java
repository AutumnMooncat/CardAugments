package CardAugments.patches.events;

import CardAugments.CardAugmentsMod;
import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractEvent;
import com.megacrit.cardcrawl.events.shrines.Transmogrifier;
import com.megacrit.cardcrawl.vfx.UpgradeShineEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardBrieflyEffect;
import javassist.CtBehavior;

import java.util.ArrayList;
import java.util.List;

public class TransmogrifierPatches {
    public static final String[] MY_TEXT = CardCrawlGame.languagePack.getUIString(CardAugmentsMod.makeID("TransmogrifierEvent")).TEXT;
    public static int myIndex = -1;
    public static boolean choseMyOption = false;
    @SpirePatch2(clz = Transmogrifier.class, method = SpirePatch.CONSTRUCTOR)
    public static class EventInit {
        @SpirePostfixPatch
        public static void addOption(Transmogrifier __instance) {
            if (CardAugmentsMod.eventAddons) {
                choseMyOption = false;
                //Rip the leave button out and put it back later
                __instance.imageEventText.clearRemainingOptions();
                myIndex = __instance.imageEventText.optionList.size();
                if (AbstractDungeon.player.masterDeck.group.stream().anyMatch(CardAugmentsMod::canReceiveModifier)) {
                    __instance.imageEventText.setDialogOption(MY_TEXT[0]);
                } else {
                    __instance.imageEventText.setDialogOption(MY_TEXT[1], true);
                }
                __instance.imageEventText.setDialogOption(MY_TEXT[2]);
            }
        }
    }

    @SpirePatch2(clz = Transmogrifier.class, method = "buttonEffect")
    public static class ButtonLogic {
        @SpirePrefixPatch
        public static SpireReturn<?> buttonPress(Transmogrifier __instance, @ByRef int[] buttonPressed, @ByRef int[] ___screenNum) {
            if (CardAugmentsMod.eventAddons) {
                if (___screenNum[0] == 0) {
                    //If we click the new leave button, let it act as if we pressed the old leave button
                    if (buttonPressed[0] == myIndex + 1) {
                        buttonPressed[0] = 1;
                        return SpireReturn.Continue();
                    }
                    if (buttonPressed[0] == myIndex) {
                        __instance.imageEventText.clearRemainingOptions();
                        __instance.imageEventText.updateBodyText(MY_TEXT[3]);
                        __instance.imageEventText.updateDialogOption(0, MY_TEXT[2]);
                        CardGroup group = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
                        for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
                            if (CardAugmentsMod.canReceiveModifier(c)) {
                                group.addToTop(c);
                            }
                        }
                        AbstractDungeon.gridSelectScreen.open(group, group.size() == 1 ? 1 : 2, MY_TEXT[4], false, false, false, false);
                        choseMyOption = true;
                        try {
                            Class<?> enumElement = Class.forName(Transmogrifier.class.getName()+"$CUR_SCREEN");
                            if (enumElement.isEnum()) {
                                Object[] enumElements = enumElement.getEnumConstants();
                                ReflectionHacks.setPrivate(__instance, Transmogrifier.class, "screen", enumElements[1]);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return SpireReturn.Return();
                    }
                }
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch2(clz = Transmogrifier.class, method = "update")
    public static class UpdateSnag {
        @SpireInsertPatch(locator = Locator.class)
        public static SpireReturn<?> update(Transmogrifier __instance) {
            if (CardAugmentsMod.eventAddons) {
                if (choseMyOption) {
                    if (!AbstractDungeon.isScreenUp && !AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
                        AbstractDungeon.topLevelEffects.add(new UpgradeShineEffect((float) Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F));
                        List<String> cardMetrics = new ArrayList<>();
                        if (AbstractDungeon.gridSelectScreen.selectedCards.size() == 1) {
                            AbstractCard c = AbstractDungeon.gridSelectScreen.selectedCards.get(0);
                            cardMetrics.add(c.cardID);
                            CardAugmentsMod.applyTrulyRandomCardMod(c);
                            AbstractDungeon.effectsQueue.add(new ShowCardBrieflyEffect(c.makeStatEquivalentCopy()));
                        } else {
                            AbstractCard c = AbstractDungeon.gridSelectScreen.selectedCards.get(0);
                            cardMetrics.add(c.cardID);
                            CardAugmentsMod.applyTrulyRandomCardMod(c);
                            AbstractDungeon.effectsQueue.add(new ShowCardBrieflyEffect(c.makeStatEquivalentCopy(), (float)Settings.WIDTH / 2.0F - 190.0F * Settings.scale, (float)Settings.HEIGHT / 2.0F));
                            c = AbstractDungeon.gridSelectScreen.selectedCards.get(1);
                            cardMetrics.add(c.cardID);
                            CardAugmentsMod.applyTrulyRandomCardMod(c);
                            AbstractDungeon.effectsQueue.add(new ShowCardBrieflyEffect(c.makeStatEquivalentCopy(), (float)Settings.WIDTH / 2.0F + 190.0F * Settings.scale, (float)Settings.HEIGHT / 2.0F));
                        }
                        AbstractDungeon.gridSelectScreen.selectedCards.clear();
                        AbstractEvent.logMetric("Transmogrifier", "Imbue", null, null, null, cardMetrics, null, null, null, 0, 0, 0, 0, 0, 0);
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
