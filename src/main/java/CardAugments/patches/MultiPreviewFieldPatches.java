package CardAugments.patches;

import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;
import com.megacrit.cardcrawl.ui.FtueTip;

import java.util.ArrayList;

public class MultiPreviewFieldPatches {
    @SpirePatch(clz = AbstractCard.class, method = "<class>")
    public static class ExtraPreviews {
        public static SpireField<ArrayList<AbstractCard>> previews = new SpireField<>(ArrayList::new);
    }

    public static void addPreview(AbstractCard card, AbstractCard preview) {
        if (card != null && preview != null) {
            ExtraPreviews.previews.get(card).add(preview);
            if (preview.cardsToPreview != null && !ExtraPreviews.previews.get(card).contains(preview.cardsToPreview)) {
                addPreview(card, preview.cardsToPreview);
            }
            for (AbstractCard c : ExtraPreviews.previews.get(preview)) {
                if (!ExtraPreviews.previews.get(card).contains(c)) {
                    addPreview(card, c);
                }
            }
        }
    }

    private static Float cardTipPad = null;

    @SpirePatch(clz = SingleCardViewPopup.class, method = "renderTips")
    public static class renderSwappablesInSingleViewPatch {

        public static void Postfix(SingleCardViewPopup __instance, SpriteBatch sb) {
            AbstractCard card = ReflectionHacks.getPrivate(__instance, SingleCardViewPopup.class, "card");
            if (!card.isLocked && card.isSeen && !ExtraPreviews.previews.get(card).isEmpty()) {
                float renderX = (1920F * Settings.scale) - (1435.0F * Settings.scale);
                float renderY = 795.0F * Settings.scale;
                if (cardTipPad == null) {
                    cardTipPad = ReflectionHacks.getPrivateStatic(AbstractCard.class, "CARD_TIP_PAD");
                }
                float horizontal = ((AbstractCard.IMG_WIDTH * 0.8F) + cardTipPad);
                Hitbox prevHb = ReflectionHacks.getPrivate(__instance, SingleCardViewPopup.class, "prevHb");
                float vertical = ((AbstractCard.IMG_HEIGHT * 0.8F) + cardTipPad);
                if (prevHb != null) {
                    vertical += prevHb.height;
                }
                boolean verticalOffset = false;
                if (card.cardsToPreview != null) {
                    renderY -= vertical;
                    verticalOffset = true;
                }
                for (AbstractCard next : ExtraPreviews.previews.get(card)) {
                    next.current_x = renderX;
                    next.current_y = renderY;
                    next.drawScale = 0.8F;
                    next.render(sb);
                    if (verticalOffset) {
                        renderY += vertical;
                        renderX -= horizontal;
                        verticalOffset = false;
                    } else {
                        renderY -= vertical;
                        verticalOffset = true;
                    }
                }
            }
        }
    }

    @SpirePatch(clz = AbstractCard.class, method = "renderCardTip")
    public static class renderSwappablesPreviewPatch {

        public static void Postfix(AbstractCard __instance, SpriteBatch sb) {
            if ((!__instance.isLocked && __instance.isSeen && !Settings.hideCards && (boolean)ReflectionHacks.getPrivate(__instance, AbstractCard.class, "renderTip") || (AbstractDungeon.screen == AbstractDungeon.CurrentScreen.FTUE) && (ReflectionHacks.getPrivate(AbstractDungeon.ftue, FtueTip.class, "c") == __instance)) && !ExtraPreviews.previews.get(__instance).isEmpty()) {
                if (AbstractDungeon.player != null && AbstractDungeon.player.isDraggingCard) {
                    return;
                }
                boolean rightSide = __instance.current_x > Settings.WIDTH * 0.75F;
                if (cardTipPad == null) {
                    cardTipPad = ReflectionHacks.getPrivateStatic(AbstractCard.class, "CARD_TIP_PAD");
                }
                float renderX = (((AbstractCard.IMG_WIDTH / 2.0F) + ((AbstractCard.IMG_WIDTH / 2.0F) * 0.8F) + (cardTipPad)) * __instance.drawScale);
                float horizontal = ((AbstractCard.IMG_WIDTH * 0.8F) + cardTipPad) * __instance.drawScale;
                if (!rightSide) {
                    horizontal *= -1;
                }
                float vertical = ((AbstractCard.IMG_HEIGHT * 0.8F) + cardTipPad) * __instance.drawScale;
                boolean verticalOffset = false;
                if (rightSide) {
                    renderX = __instance.current_x + renderX;
                } else {
                    renderX = __instance.current_x - renderX;
                }
                float renderY = __instance.current_y + ((AbstractCard.IMG_HEIGHT / 2.0F) - (AbstractCard.IMG_HEIGHT / 2.0F * 0.8F)) * __instance.drawScale;
                if (__instance.cardsToPreview != null) {
//                    renderY -= vertical;
//                    verticalOffset = true;
                    renderX += horizontal;
                }
                for (AbstractCard next : ExtraPreviews.previews.get(__instance)) {
                    next.current_x = renderX;
                    next.current_y = renderY;
                    next.drawScale = __instance.drawScale * 0.8F;
                    next.render(sb);
                    renderX += horizontal;
//                    if (verticalOffset) {
//                        renderY += vertical;
//                        renderX += horizontal;
//                        verticalOffset = false;
//                    } else {
//                        renderY -= vertical;
//                        verticalOffset = true;
//                    }
                }
            }
        }
    }
}
