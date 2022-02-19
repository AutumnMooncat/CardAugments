package CardAugments.patches;

import CardAugments.CardAugmentsMod;
import CardAugments.cardmods.AbstractAugment;
import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.shrines.GremlinMatchGame;
import com.megacrit.cardcrawl.neow.NeowReward;
import com.megacrit.cardcrawl.screens.select.GridCardSelectScreen;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import javassist.CtBehavior;
import mintySpire.patches.cards.betterUpdatePreview.CardFields;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class OnCardGeneratedPatches {

    @SpirePatch2(clz = AbstractDungeon.class, method = "getRewardCards")
    public static class ModifySpawnedCardsPatch {
        @SpirePostfixPatch
        public static void patch(ArrayList<AbstractCard> __result) {
            for (AbstractCard c : __result) {
                rollCardAugment(c, __result.indexOf(c));
            }
        }
    }

    @SpirePatch2(clz = AbstractDungeon.class, method = "getColorlessRewardCards")
    public static class ModifySpawnedColorlessCardsPatch {
        @SpirePostfixPatch
        public static void patch(ArrayList<AbstractCard> __result) {
            for (AbstractCard c : __result) {
                rollCardAugment(c, __result.indexOf(c));
            }
        }
    }

    @SpirePatch2(clz = GridCardSelectScreen.class, method = "openConfirmationGrid")
    public static class ModifyConfirmScreenCards {
        @SpirePostfixPatch
        public static void patch(CardGroup group) {
            for (AbstractCard c : group.group) {
                rollCardAugment(c);
            }
        }
    }

    @SpirePatch2(clz = GremlinMatchGame.class, method = "initializeCards")
    public static class ModifyMatchGameCards {
        @SpireInsertPatch(locator = Locator.class, localvars = "retVal")
        public static void patch(ArrayList<AbstractCard> retVal) {
            for (AbstractCard c : retVal) {
                rollCardAugment(c);
            }
        }
        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractPlayer.class, "getStartCardForEvent");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }

    @SpirePatch2(clz = NeowReward.class, method = "getRewardCards")
    public static class ModifyNeowRewardCardsPatch {
        @SpirePostfixPatch
        public static void patch(ArrayList<AbstractCard> __result) {
            for (AbstractCard c : __result) {
                rollCardAugment(c);
            }
        }
    }

    @SpirePatch2(clz = NeowReward.class, method = "getColorlessRewardCards")
    public static class ModifyNeowRewardColorlessCardsPatch {
        @SpirePostfixPatch
        public static void patch(ArrayList<AbstractCard> __result) {
            for (AbstractCard c : __result) {
                rollCardAugment(c);
            }
        }
    }

    @SpirePatch2(clz = AbstractPlayer.class, method = "initializeStarterDeck")
    public static class ModifyStarterCards {
        @SpirePostfixPatch
        public static void patch(AbstractPlayer __instance) {
            if (CardAugmentsMod.modifyStarters) {
                for (AbstractCard c : __instance.masterDeck.group) {
                    rollCardAugment(c);
                }
            }
        }
    }

    @SpirePatch2(clz = ShowCardAndObtainEffect.class, method = SpirePatch.CONSTRUCTOR, paramtypez = {AbstractCard.class, float.class, float.class, boolean.class})
    public static class ModifySpawnedMasterDeckCards {
        @SpirePostfixPatch
        public static void patch(AbstractCard ___card) {
            rollCardAugment(___card);
        }
    }

    public static void rollCardAugment(AbstractCard c) {
        rollCardAugment(c, -1);
    }

    public static void rollCardAugment(AbstractCard c, int index) {
        if (AbstractDungeon.miscRng.random(99) < CardAugmentsMod.modProbabilityPercent) {
            applyWeightedCardMod(c, rollRarity(), index);
        }
    }

    public static AbstractAugment.AugmentRarity rollRarity() {
        int roll = AbstractDungeon.miscRng.random(CardAugmentsMod.commonWeight + CardAugmentsMod.uncommonWeight + CardAugmentsMod.rareWeight - 1);
        if (roll < CardAugmentsMod.commonWeight) {
            return AbstractAugment.AugmentRarity.COMMON;
        } else if (roll < CardAugmentsMod.commonWeight + CardAugmentsMod.uncommonWeight) {
            return AbstractAugment.AugmentRarity.UNCOMMON;
        } else {
            return AbstractAugment.AugmentRarity.RARE;
        }
    }

    public static void applyWeightedCardMod(AbstractCard c, AbstractAugment.AugmentRarity rarity, int index) {
        ArrayList<AbstractAugment> validMods = new ArrayList<>();
        switch (rarity) {
            case COMMON:
                validMods.addAll(CardAugmentsMod.commonMods.stream().filter(m -> m.shouldApply(c)).collect(Collectors.toCollection(ArrayList::new)));
                break;
            case UNCOMMON:
                validMods.addAll(CardAugmentsMod.uncommonMods.stream().filter(m -> m.shouldApply(c)).collect(Collectors.toCollection(ArrayList::new)));
                break;
            case RARE:
                validMods.addAll(CardAugmentsMod.rareMods.stream().filter(m -> m.shouldApply(c)).collect(Collectors.toCollection(ArrayList::new)));
                break;
        }
        if (!validMods.isEmpty()) {
            AbstractCardModifier m = validMods.get(AbstractDungeon.miscRng.random(validMods.size()-1)).makeCopy();
            CardModifierManager.addModifier(c, m);
            if (index != -1 && CardAugmentsMod.isMintyLoaded) {
                CardModifierManager.addModifier(CardFields.SCVPopup.unupgradedCardRewards.get(CardCrawlGame.cardPopup).get(index), m);
            }
        }
    }
}
