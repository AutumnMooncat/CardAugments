package CardAugments.patches;

import CardAugments.CardAugmentsMod;
import CardAugments.cardmods.AbstractAugment;
import CardAugments.screens.ModifierScreen;
import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.mod.stslib.ui.MultiUpgradeTree;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.MathHelper;
import com.megacrit.cardcrawl.helpers.controller.CInputActionSet;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;
import com.megacrit.cardcrawl.screens.mainMenu.ScrollBar;
import com.megacrit.cardcrawl.screens.mainMenu.ScrollBarListener;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.FieldAccess;
import javassist.expr.MethodCall;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

public class SCVPatches {
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(CardAugmentsMod.makeID("SCVScreen"));
    public static final String[] TEXT = uiStrings.TEXT;
    public static final Hitbox augmentHitbox = new Hitbox(320.0F * Settings.scale, 80.0F * Settings.scale);
    public static boolean viewingAugments = false;
    public static final CardGroup cardsToRender = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
    public static final PreviewScreen screen = new PreviewScreen();

    @SpirePatch2(clz = SingleCardViewPopup.class, method = "update")
    public static class UpdatePatch {
        @SpirePostfixPatch
        public static void updateTime(SingleCardViewPopup __instance, AbstractCard ___card) {
            if (viewingAugments) {
                screen.update();
            }
            augmentHitbox.update();// 231
            if (augmentHitbox.hovered && InputHelper.justClickedLeft) {// 233
                augmentHitbox.clickStarted = true;// 234
            }

            if (augmentHitbox.clicked || CInputActionSet.proceed.isJustPressed()) {// 237
                CInputActionSet.proceed.unpress();// 238
                augmentHitbox.clicked = false;// 239
                viewingAugments = !viewingAugments;// 240
                cardsToRender.clear();
                if (viewingAugments) {
                    ArrayList<AbstractAugment> validAugments = CardAugmentsMod.getAllValidMods(___card);
                    Collections.sort(validAugments, Comparator.comparing(o -> o.identifier(null)));
                    for (AbstractAugment a : validAugments) {
                        AbstractCard copy = ___card.makeStatEquivalentCopy();
                        CardModifierManager.addModifier(copy, a.makeCopy());
                        copy.targetDrawScale = 0.75f;
                        cardsToRender.addToTop(copy);
                    }
                    screen.calculateScrollBounds();
                }
            }
        }
    }

    @SpirePatch2(clz = SingleCardViewPopup.class, method = "render")
    public static class RenderPatch {
        @SpirePostfixPatch
        public static void renderTime(SingleCardViewPopup __instance, SpriteBatch sb) {
            if (viewingAugments) {
                screen.render(sb);
            }

            FontHelper.cardTitleFont.getData().setScale(1.0F);
            sb.setColor(Color.WHITE);// 1712
            sb.draw(ImageMaster.CHECKBOX, augmentHitbox.cX - 120.0F * Settings.scale - 32.0F, augmentHitbox.cY - 32.0F, 32.0F, 32.0F, 64.0F, 64.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 64, 64, false, false);// 1713
            if (augmentHitbox.hovered) {// 1731
                FontHelper.renderFont(sb, FontHelper.cardTitleFont, TEXT[0], augmentHitbox.cX - 85.0F * Settings.scale, augmentHitbox.cY + 10.0F * Settings.scale, Settings.BLUE_TEXT_COLOR);// 1732
            } else {
                FontHelper.renderFont(sb, FontHelper.cardTitleFont, TEXT[0], augmentHitbox.cX - 85.0F * Settings.scale, augmentHitbox.cY + 10.0F * Settings.scale, Settings.GOLD_COLOR);// 1740
            }

            if (viewingAugments) {// 1749
                sb.setColor(Color.WHITE);// 1750
                sb.draw(ImageMaster.TICK, augmentHitbox.cX - 120.0F * Settings.scale - 32.0F, augmentHitbox.cY - 32.0F, 32.0F, 32.0F, 64.0F, 64.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 64, 64, false, false);// 1751
            }

            augmentHitbox.render(sb);
        }
    }

    @SpirePatch2(clz = SingleCardViewPopup.class, method = "open", paramtypez = {AbstractCard.class})
    @SpirePatch2(clz = SingleCardViewPopup.class, method = "open", paramtypez = {AbstractCard.class, CardGroup.class})
    public static class OpenPatch {
        @SpirePostfixPatch
        public static void openTime(SingleCardViewPopup __instance) {
            augmentHitbox.move(155F * Settings.scale, 70.0F * Settings.scale);
        }
    }

    @SpirePatch2(clz = SingleCardViewPopup.class, method = "close")
    public static class ClosePatch {
        @SpirePostfixPatch
        public static void closeTime(SingleCardViewPopup __instance) {
            viewingAugments = false;
            cardsToRender.clear();
        }
    }

    @SpirePatch2(clz = SingleCardViewPopup.class, method = "updateInput")
    public static class UpdateInputPatch {
        @SpirePrefixPatch
        public static SpireReturn<?> myButtonStuff(SingleCardViewPopup __instance) {
            if (InputHelper.justClickedLeft) {
                if (augmentHitbox.hovered || viewingAugments) {
                    return SpireReturn.Return();
                }
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(clz = SingleCardViewPopup.class, method = "renderTips")
    public static class TipsBeGone {
        @SpirePrefixPatch
        public static SpireReturn<?> stop(SingleCardViewPopup __instance, SpriteBatch sb, AbstractCard ___card) {
            if (viewingAugments) {
                return SpireReturn.Return();
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch2(clz = MultiUpgradeTree.class, method = "update")
    @SpirePatch2(clz = MultiUpgradeTree.class, method = "render")
    public static class ClobberTime {
        @SpirePrefixPatch
        public static SpireReturn<?> ceaseAndDesist() {
            if (viewingAugments) {
                return SpireReturn.Return();
            }
            return SpireReturn.Continue();
        }
    }

    public static class PreviewScreen implements ScrollBarListener {
        private static final int CARDS_PER_LINE = (int)((float)Settings.WIDTH / (AbstractCard.IMG_WIDTH * 0.75F + Settings.CARD_VIEW_PAD_X * 3.0F));
        private static float drawStartX;
        private static final float drawStartY = (float)Settings.HEIGHT * 0.8F;
        private static final float padX = AbstractCard.IMG_WIDTH * 0.75F + Settings.CARD_VIEW_PAD_X;
        private static final float padY = AbstractCard.IMG_HEIGHT * 0.75F + Settings.CARD_VIEW_PAD_Y;
        private boolean grabbedScreen = false;
        private float grabStartY = 0.0F;
        private float currentDiffY = 0.0F;
        private float scrollLowerBound;
        private float scrollUpperBound;
        private boolean justSorted;
        public AbstractCard hoveredCard;
        private ScrollBar scrollBar;

        public PreviewScreen() {
            scrollBar = new ScrollBar(PreviewScreen.this);
            scrollLowerBound = -Settings.DEFAULT_SCROLL_LIMIT;// 47
            scrollUpperBound = Settings.DEFAULT_SCROLL_LIMIT;// 48

            drawStartX = (float)Settings.WIDTH;// 71
            drawStartX -= (float)(CARDS_PER_LINE - 0) * AbstractCard.IMG_WIDTH * 0.75F;// 72
            drawStartX -= (float)(CARDS_PER_LINE - 1) * Settings.CARD_VIEW_PAD_X;// 73
            drawStartX /= 2.0F;// 74
            drawStartX += AbstractCard.IMG_WIDTH * 0.75F / 2.0F;// 75
        }

        public void update() {
            boolean isScrollBarScrolling = scrollBar.update();
            if (!isScrollBarScrolling) {
                updateScrolling();
            }
            updateCards();
        }

        public void render(SpriteBatch sb) {
            sb.setColor(new Color(0.0F, 0.0F, 0.0F, 0.8F));
            sb.draw(ImageMaster.WHITE_SQUARE_IMG, 0.0F, 0.0F, (float)Settings.WIDTH, (float)Settings.HEIGHT - 64.0F * Settings.scale);
            scrollBar.render(sb);
            renderCards(sb);
        }

        private void updateCards() {
            this.hoveredCard = null;// 337
            int lineNum = 0;// 338
            ArrayList<AbstractCard> cards = cardsToRender.group;// 340

            for(int i = 0; i < cards.size(); ++i) {// 342
                int mod = i % CARDS_PER_LINE;// 343
                if (mod == 0 && i != 0) {// 344
                    ++lineNum;// 345
                }

                cards.get(i).target_x = drawStartX + (float)mod * padX;// 347
                cards.get(i).target_y = drawStartY + this.currentDiffY - (float)lineNum * padY;// 348
                cards.get(i).update();// 349
                cards.get(i).updateHoverLogic();// 350
                if (cards.get(i).hb.hovered) {// 352
                    this.hoveredCard = cards.get(i);// 353
                }
            }

            if (justSorted) {// 356
                AbstractCard c;
                for(Iterator var5 = cards.iterator(); var5.hasNext(); c.current_y = c.target_y) {// 357 359
                    c = (AbstractCard)var5.next();
                    c.current_x = c.target_x;// 358
                }

                justSorted = false;// 361
            }

        }

        public void renderCards(SpriteBatch sb) {
            for (AbstractCard c : cardsToRender.group) {
                for (AbstractCardModifier m : CardModifierManager.modifiers(c)) {
                    String s = m.identifier(c);
                }
            }
            cardsToRender.renderInLibrary(sb);// 503
            cardsToRender.renderTip(sb);
            if (this.hoveredCard != null) {// 426
                this.hoveredCard.renderHoverShadow(sb);// 427
                this.hoveredCard.renderInLibrary(sb);// 428
            }
        }

        private void updateScrolling() {
            int y = InputHelper.mY;// 366
            if (!this.grabbedScreen) {// 368
                if (InputHelper.scrolledDown) {// 369
                    this.currentDiffY += Settings.SCROLL_SPEED;// 370
                } else if (InputHelper.scrolledUp) {// 371
                    this.currentDiffY -= Settings.SCROLL_SPEED;// 372
                }

                if (InputHelper.justClickedLeft) {// 375
                    this.grabbedScreen = true;// 376
                    this.grabStartY = (float)y - this.currentDiffY;// 377
                }
            } else if (InputHelper.isMouseDown) {// 380
                this.currentDiffY = (float)y - this.grabStartY;// 381
            } else {
                this.grabbedScreen = false;// 383
            }

            this.resetScrolling();// 387
            this.updateBarPosition();// 388
        }// 389

        public void calculateScrollBounds() {
            int size = cardsToRender.size();// 395
            int scrollTmp = 0;// 397
            if (size > CARDS_PER_LINE * 2) {// 398
                scrollTmp = size / CARDS_PER_LINE - 2;// 399
                if (size % CARDS_PER_LINE != 0) {// 400
                    ++scrollTmp;// 401
                }

                this.scrollUpperBound = Settings.DEFAULT_SCROLL_LIMIT + (float)scrollTmp * padY;// 403
            } else {
                this.scrollUpperBound = Settings.DEFAULT_SCROLL_LIMIT;// 405
            }

        }// 407

        private void resetScrolling() {
            if (this.currentDiffY < this.scrollLowerBound) {// 413
                this.currentDiffY = MathHelper.scrollSnapLerpSpeed(this.currentDiffY, this.scrollLowerBound);// 414
            } else if (this.currentDiffY > this.scrollUpperBound) {// 415
                this.currentDiffY = MathHelper.scrollSnapLerpSpeed(this.currentDiffY, this.scrollUpperBound);// 416
            }

        }// 418

        public void scrolledUsingBar(float newPercent) {
            this.currentDiffY = MathHelper.valueFromPercentBetween(this.scrollLowerBound, this.scrollUpperBound, newPercent);// 546
            this.updateBarPosition();// 547
        }// 548

        private void updateBarPosition() {
            float percent = MathHelper.percentFromValueBetween(this.scrollLowerBound, this.scrollUpperBound, this.currentDiffY);// 551
            this.scrollBar.parentScrolledToPercent(percent);// 552
        }// 553
    }
}
