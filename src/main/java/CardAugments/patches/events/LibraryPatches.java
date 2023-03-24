package CardAugments.patches.events;

import CardAugments.CardAugmentsMod;
import CardAugments.cardmods.AbstractAugment;
import basemod.helpers.CardModifierManager;
import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractEvent;
import com.megacrit.cardcrawl.events.city.TheLibrary;
import com.megacrit.cardcrawl.vfx.UpgradeShineEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardBrieflyEffect;
import javassist.CtBehavior;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class LibraryPatches {
    public static final String[] MY_TEXT = CardCrawlGame.languagePack.getUIString(CardAugmentsMod.makeID("LibraryEvent")).TEXT;
    public static final int MODIFIERS = 5;
    public static int myIndex = -1;
    public static boolean choseMyOption = false;
    public static boolean choseCard = false;
    public static AbstractCard pickedCard = null;
    static HashMap<AbstractCard, AbstractAugment> augmentMap = new HashMap<>();
    @SpirePatch2(clz = TheLibrary.class, method = SpirePatch.CONSTRUCTOR)
    public static class LibraryInit {
        @SpirePostfixPatch
        public static void addOption(TheLibrary __instance) {
            if (CardAugmentsMod.eventAddons) {
                choseMyOption = false;
                choseCard = false;
                pickedCard = null;
                augmentMap.clear();
                myIndex = __instance.imageEventText.optionList.size();
                if (AbstractDungeon.player.masterDeck.group.stream().anyMatch(CardAugmentsMod::canReceiveModifier)) {
                    __instance.imageEventText.setDialogOption(MY_TEXT[0]);
                } else {
                    __instance.imageEventText.setDialogOption(MY_TEXT[1], false);
                }
            }
        }
    }

    @SpirePatch2(clz = TheLibrary.class, method = "buttonEffect")
    public static class ButtonLogic {
        @SpirePrefixPatch
        public static SpireReturn<?> buttonPress(TheLibrary __instance, @ByRef int[] buttonPressed, @ByRef int[] ___screenNum) {
            if (CardAugmentsMod.eventAddons) {
                if (___screenNum[0] == 0) {
                    if (buttonPressed[0] == myIndex) {
                        __instance.imageEventText.clearRemainingOptions();
                        __instance.imageEventText.updateBodyText(MY_TEXT[3]);
                        __instance.imageEventText.updateDialogOption(0, MY_TEXT[2]);
                        CardGroup group = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
                        for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
                            if (CardAugmentsMod.canReceiveModifier(c)) {
                                group.addToBottom(c);
                            }
                        }
                        AbstractDungeon.gridSelectScreen.open(group, 1, MY_TEXT[4], false, false, false, false);
                        choseMyOption = true;
                        ___screenNum[0] = 2;
                        return SpireReturn.Return();
                    }
                }
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch2(clz = TheLibrary.class, method = "update")
    public static class UpdateSnag {
        @SpireInsertPatch(locator = Locator.class)
        public static SpireReturn<?> update(TheLibrary __instance) {
            if (CardAugmentsMod.eventAddons) {
                if (choseMyOption) {
                    if (!AbstractDungeon.isScreenUp && !AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
                        if (!choseCard) {
                            pickedCard = AbstractDungeon.gridSelectScreen.selectedCards.get(0);
                            choseCard = true;
                            CardGroup group = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
                            ArrayList<AbstractAugment> validMods = new ArrayList<>();
                            for (int i = 0 ; i < MODIFIERS ; i ++) {
                                AbstractCard copy = pickedCard.makeStatEquivalentCopy();
                                if (validMods.isEmpty()) {
                                    validMods = CardAugmentsMod.getAllValidMods(copy);
                                }
                                AbstractAugment a = (AbstractAugment) validMods.remove(AbstractDungeon.miscRng.random(validMods.size()-1)).makeCopy();
                                if (a != null) {
                                    CardModifierManager.addModifier(copy, a);
                                    augmentMap.put(copy, a);
                                    group.addToBottom(copy);
                                }
                            }
                            AbstractDungeon.gridSelectScreen.open(group, 1, MY_TEXT[5], false, false, false, false);
                        } else {
                            AbstractCard c = AbstractDungeon.gridSelectScreen.selectedCards.get(0);
                            CardModifierManager.addModifier(pickedCard, augmentMap.get(c));
                            AbstractDungeon.effectsQueue.add(new ShowCardBrieflyEffect(c.makeStatEquivalentCopy()));
                            AbstractDungeon.topLevelEffects.add(new UpgradeShineEffect((float) Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F));
                            AbstractDungeon.gridSelectScreen.selectedCards.clear();
                            AbstractEvent.logMetricCardUpgrade("The Library", "Rewrite", c);
                            try {
                                CardAugmentsMod.griefLibrary = true;
                                CardAugmentsMod.cardAugmentsConfig.setBool(CardAugmentsMod.GRIEF_LIBRARY, true);
                                CardAugmentsMod.cardAugmentsConfig.save();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            choseMyOption = false;
                        }
                    }
                    return SpireReturn.Return();
                }
            }
            return SpireReturn.Continue();
        }

        @SpirePatch2(clz = TheLibrary.class, method = "getBook")
        public static class LibraryGrief {
            @SpirePostfixPatch
            public static String getString(String __result) {
                if (CardAugmentsMod.griefLibrary) {
                    ArrayList<String> list = new ArrayList<>();
                    list.add(MY_TEXT[6]);
                    list.add(MY_TEXT[7]);
                    list.add(MY_TEXT[8]);
                    __result = list.get(MathUtils.random(2));
                    try {
                        CardAugmentsMod.griefLibrary = false;
                        CardAugmentsMod.cardAugmentsConfig.setBool(CardAugmentsMod.GRIEF_LIBRARY, false);
                        CardAugmentsMod.cardAugmentsConfig.save();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return __result;
            }
        }

        public static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                Matcher m = new Matcher.FieldAccessMatcher(TheLibrary.class, "pickCard");
                return LineFinder.findInOrder(ctBehavior, m);
            }
        }
    }
}
