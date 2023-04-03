package CardAugments.screens;

import CardAugments.CardAugmentsMod;
import CardAugments.cardmods.AbstractAugment;
import CardAugments.patches.MainMenuPatches;
import CardAugments.util.FormatHelper;
import basemod.BaseMod;
import basemod.ReflectionHacks;
import basemod.helpers.CardModifierManager;
import basemod.patches.com.megacrit.cardcrawl.screens.compendium.CardLibraryScreen.NoCompendium;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireInstrumentPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.characters.TheSilent;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.GameCursor;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.*;
import com.megacrit.cardcrawl.helpers.controller.CInputActionSet;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;
import com.megacrit.cardcrawl.screens.mainMenu.MainMenuScreen;
import com.megacrit.cardcrawl.screens.mainMenu.MenuCancelButton;
import com.megacrit.cardcrawl.screens.mainMenu.ScrollBar;
import com.megacrit.cardcrawl.screens.mainMenu.ScrollBarListener;
import com.megacrit.cardcrawl.screens.options.DropdownMenu;
import com.megacrit.cardcrawl.screens.options.DropdownMenuListener;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

import java.lang.reflect.Array;
import java.util.*;

public class ModifierScreen implements DropdownMenuListener, ScrollBarListener {
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(CardAugmentsMod.makeID("ModifierScreen"));
    public static final String[] TEXT = uiStrings.TEXT;
    private static final int CARDS_PER_LINE = (int)((float)Settings.WIDTH / (AbstractCard.IMG_WIDTH * 0.75F + Settings.CARD_VIEW_PAD_X * 3.0F));
    private static final float DROPDOWN_X = 20f * Settings.scale;
    private static final float MOD_DROPDOWN_Y = Settings.HEIGHT/2f + 280.0F * Settings.scale;
    private static final float AUGMENT_DROPDOWN_Y = MOD_DROPDOWN_Y - 60F * Settings.scale;
    private static final float CHARACTER_DROPDOWN_Y = AUGMENT_DROPDOWN_Y - 60F * Settings.scale;
    private static final float RARITY_Y = CHARACTER_DROPDOWN_Y - 50f * Settings.scale;
    private static final float VALID_CARDS_Y = RARITY_Y - 50f * Settings.scale;
    private static final float HB_X = DROPDOWN_X + 100f * Settings.scale;
    private static final float HB_Y = VALID_CARDS_Y - 60f * Settings.scale;
    private static float drawStartX;
    private static final float drawStartY = (float)Settings.HEIGHT * 0.8F; //0.66
    private static final float padX = AbstractCard.IMG_WIDTH * 0.75F + Settings.CARD_VIEW_PAD_X;
    private static final float padY = AbstractCard.IMG_HEIGHT * 0.75F + Settings.CARD_VIEW_PAD_Y;
    private boolean grabbedScreen = false;
    private float grabStartY = 0.0F;
    private float currentDiffY = 0.0F;
    private float scrollLowerBound;
    private float scrollUpperBound;
    private boolean justSorted;
    private final HashMap<String, AbstractAugment> augmentMap = new HashMap<>();
    private AbstractAugment selectedAugment;
    private String selectedModID;
    private final CardGroup validCards;
    private final CardGroup cardsToRender;
    private AbstractCard hoveredCard;
    private AbstractCard clickStartedCard;
    private final MenuCancelButton cancelButton;
    private DropdownMenu modDropdown;
    private DropdownMenu augmentDropdown;
    private DropdownMenu characterDropdown;
    private AbstractCard.CardColor colorFilter;
    private HashMap<String, AbstractCard.CardColor> colorMap = new HashMap<>();
    private ScrollBar scrollBar;
    private Hitbox upgradeHb;
    private boolean upgradePreview;
    private boolean ignoreScrollReset;
    
    public ModifierScreen() {
        upgradeHb = new Hitbox(250.0F * Settings.scale, 80.0F * Settings.scale);
        cancelButton = new MenuCancelButton();

        validCards = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
        cardsToRender = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);

        modDropdown = new DropdownMenu(this, getModStrings(), FontHelper.tipBodyFont, Settings.CREAM_COLOR);
        augmentDropdown = new DropdownMenu(this, getAugmentStrings(), FontHelper.tipBodyFont, Settings.CREAM_COLOR);
        characterDropdown = new DropdownMenu(this, getCharacterStrings(), FontHelper.tipBodyFont, Settings.CREAM_COLOR);

        scrollBar = new ScrollBar(this);
        scrollLowerBound = -Settings.DEFAULT_SCROLL_LIMIT;// 47
        scrollUpperBound = Settings.DEFAULT_SCROLL_LIMIT;// 48

        drawStartX = (float)Settings.WIDTH;// 71
        drawStartX -= (float)(CARDS_PER_LINE - 0) * AbstractCard.IMG_WIDTH * 0.75F;// 72
        drawStartX -= (float)(CARDS_PER_LINE - 1) * Settings.CARD_VIEW_PAD_X;// 73
        drawStartX /= 2.0F;// 74
        drawStartX += AbstractCard.IMG_WIDTH * 0.75F / 2.0F;// 75
    }
    
    public void open() {
        SingleCardViewPopup.isViewingUpgrade = false;
        upgradePreview = false;
        refreshDropdownMenu(modDropdown);
        cancelButton.show(TEXT[0]);
        CardCrawlGame.mainMenuScreen.screen = MainMenuPatches.Enums.MODIFIERS_VIEW; //This is how we tell it what screen is open
        CardCrawlGame.mainMenuScreen.darken();
        upgradeHb.move(HB_X, HB_Y);
    }

    public void update() {
        if (modDropdown.isOpen) {
            modDropdown.update();
        } else if (augmentDropdown.isOpen) {
            augmentDropdown.update();
        } else if (characterDropdown.isOpen) {
            characterDropdown.update();
        } else {
            updateButtons();
            boolean isScrollBarScrolling = scrollBar.update();// 186
            if (!CardCrawlGame.cardPopup.isOpen && !isScrollBarScrolling) {// 187
                updateScrolling();// 188
            }
            this.upgradeHb.update();// 231
            if (this.upgradeHb.hovered && InputHelper.justClickedLeft) {// 233
                this.upgradeHb.clickStarted = true;// 234
            }

            if (this.upgradeHb.clicked || CInputActionSet.proceed.isJustPressed()) {// 237
                CInputActionSet.proceed.unpress();// 238
                this.upgradeHb.clicked = false;// 239
                upgradePreview = !upgradePreview;// 240
                ignoreScrollReset = true;
                refreshDropdownMenu(augmentDropdown);
            }
            updateCards();
            modDropdown.update();
            augmentDropdown.update();
            characterDropdown.update();
            if (this.hoveredCard != null) {// 166
                CardCrawlGame.cursor.changeType(GameCursor.CursorType.INSPECT);// 167
                if (InputHelper.justClickedLeft) {// 168
                    this.clickStartedCard = this.hoveredCard;// 169
                }

                if (InputHelper.justReleasedClickLeft && this.clickStartedCard != null && this.hoveredCard != null || this.hoveredCard != null && CInputActionSet.select.isJustPressed()) {// 171 172
                    if (Settings.isControllerMode) {// 174
                        this.clickStartedCard = this.hoveredCard;// 175
                    }

                    InputHelper.justReleasedClickLeft = false;// 178
                    CardCrawlGame.cardPopup.open(this.clickStartedCard, cardsToRender);// 179
                    this.clickStartedCard = null;// 180
                }
            } else {
                this.clickStartedCard = null;// 183
            }
        }
    }

    public void updateButtons() {
        cancelButton.update();
        if (cancelButton.hb.clicked || InputHelper.pressedEscape) {
            CardCrawlGame.mainMenuScreen.superDarken = false;
            InputHelper.pressedEscape = false;
            cancelButton.hb.clicked = false;
            cancelButton.hide();
            CardCrawlGame.mainMenuScreen.screen = MainMenuScreen.CurScreen.MAIN_MENU;
            CardCrawlGame.mainMenuScreen.lighten();
            selectedAugment = null;
            cardsToRender.clear();
        }
    }

    public void render(SpriteBatch sb) {
        scrollBar.render(sb);
        cancelButton.render(sb);
        renderUpgradeViewToggle(sb);
        renderInfo(sb);
        characterDropdown.render(sb, DROPDOWN_X, CHARACTER_DROPDOWN_Y);
        augmentDropdown.render(sb, DROPDOWN_X, AUGMENT_DROPDOWN_Y);
        modDropdown.render(sb, DROPDOWN_X, MOD_DROPDOWN_Y);
        renderCards(sb);
    }

    public ArrayList<String> getModStrings() {
        ArrayList<String> ret = new ArrayList<>();
        for (AbstractAugment a : CardAugmentsMod.crossoverMap.keySet()) {
            String s = CardAugmentsMod.crossoverMap.get(a);
            if (!ret.contains(s)) {
                ret.add(s);
            }
        }
        Collections.sort(ret);
        return ret;
    }

    public ArrayList<String> getAugmentStrings() {
        ArrayList<String> ret = new ArrayList<>();
        augmentMap.clear();
        for (String id : CardAugmentsMod.modMap.keySet()) {
            AbstractAugment a = CardAugmentsMod.modMap.get(id);
            if (CardAugmentsMod.crossoverMap.get(a).equals(selectedModID)) {
                String s = formatText(id);
                ret.add(s);
                augmentMap.put(s, a);
            }
        }
        Collections.sort(ret);
        //ret.replaceAll(this::formatText);
        return ret;
    }

    public ArrayList<String> getCharacterStrings() {
        ArrayList<String> ret = new ArrayList<>();
        HashSet<AbstractCard.CardColor> checkedColors = new HashSet<>();
        colorMap.clear();

        ret.add(TEXT[4]);
        colorMap.put(TEXT[4], null);
        ret.add(BaseMod.findCharacter(AbstractPlayer.PlayerClass.IRONCLAD).getLocalizedCharacterName());
        colorMap.put(BaseMod.findCharacter(AbstractPlayer.PlayerClass.IRONCLAD).getLocalizedCharacterName(), AbstractCard.CardColor.RED);
        checkedColors.add(AbstractCard.CardColor.RED);
        ret.add(BaseMod.findCharacter(AbstractPlayer.PlayerClass.THE_SILENT).getLocalizedCharacterName());
        colorMap.put(BaseMod.findCharacter(AbstractPlayer.PlayerClass.THE_SILENT).getLocalizedCharacterName(), AbstractCard.CardColor.GREEN);
        checkedColors.add(AbstractCard.CardColor.GREEN);
        ret.add(BaseMod.findCharacter(AbstractPlayer.PlayerClass.DEFECT).getLocalizedCharacterName());
        colorMap.put(BaseMod.findCharacter(AbstractPlayer.PlayerClass.DEFECT).getLocalizedCharacterName(), AbstractCard.CardColor.BLUE);
        checkedColors.add(AbstractCard.CardColor.BLUE);
        ret.add(BaseMod.findCharacter(AbstractPlayer.PlayerClass.WATCHER).getLocalizedCharacterName());
        colorMap.put(BaseMod.findCharacter(AbstractPlayer.PlayerClass.WATCHER).getLocalizedCharacterName(), AbstractCard.CardColor.PURPLE);
        checkedColors.add(AbstractCard.CardColor.PURPLE);
        ret.add(FormatHelper.capitalize(AbstractCard.CardColor.COLORLESS.toString()));
        colorMap.put(FormatHelper.capitalize(AbstractCard.CardColor.COLORLESS.toString()), AbstractCard.CardColor.COLORLESS);
        checkedColors.add(AbstractCard.CardColor.COLORLESS);
        ret.add(FormatHelper.capitalize(AbstractCard.CardColor.CURSE.toString()));
        colorMap.put(FormatHelper.capitalize(AbstractCard.CardColor.CURSE.toString()), AbstractCard.CardColor.CURSE);
        checkedColors.add(AbstractCard.CardColor.CURSE);

        ArrayList<String> modChars = new ArrayList<>();
        for (AbstractCard c : CardLibrary.getAllCards()) {
            if (!c.getClass().isAnnotationPresent(NoCompendium.class)) {
                AbstractCard.CardColor color = c.color;
                if (!checkedColors.contains(color)) {
                    checkedColors.add(color);
                    AbstractPlayer.PlayerClass playerClass = null;
                    for (AbstractPlayer character : CardCrawlGame.characterManager.getAllCharacters()) {
                        if (character.getCardColor().equals(color)) {
                            playerClass = character.chosenClass;
                            break;
                        }
                    }
                    String name = playerClass != null ? BaseMod.findCharacter(playerClass).getLocalizedCharacterName() : FormatHelper.capitalize(color.toString());
                    modChars.add(name);
                    colorMap.put(name, c.color);
                }
            }
        }
        Collections.sort(modChars);
        ret.addAll(modChars);
        return ret;
    }

    public String formatText(String s) {
        int index = s.lastIndexOf(":");
        if (index != -1) {
            return s.substring(index+1);
        }
        return s;
    }

    @Override
    public void changedSelectionTo(DropdownMenu dropdownMenu, int i, String s) {
        if (dropdownMenu == augmentDropdown) {
            validCards.clear();
            selectedAugment = augmentMap.get(s);
            for (AbstractCard c : CardLibrary.getAllCards()) {
                if (!c.getClass().isAnnotationPresent(NoCompendium.class)) {
                    if (selectedAugment.validCard(c)) {
                        AbstractCard copy = c.makeCopy();
                        CardModifierManager.addModifier(copy, selectedAugment.makeCopy());
                        if (upgradePreview && copy.canUpgrade()) {
                            copy.upgrade();
                            copy.displayUpgrades();
                        }
                        copy.targetDrawScale = 0.75f;
                        validCards.addToBottom(copy);
                    }
                }
            }
            validCards.sortAlphabetically(true);// 143
            validCards.sortByRarity(true);// 144
            validCards.group.sort(Comparator.comparing(card -> card.color));
            validCards.sortByStatus(true);
            updateCardFilters();
        }
        if (dropdownMenu == modDropdown) {
            selectedModID = s;
            augmentDropdown = new DropdownMenu(this, getAugmentStrings(), FontHelper.tipBodyFont, Settings.CREAM_COLOR);
            refreshDropdownMenu(augmentDropdown);
        }
        if (dropdownMenu == characterDropdown) {
            colorFilter = colorMap.getOrDefault(s, null);
            updateCardFilters();
        }
    }

    private void updateCardFilters() {
        cardsToRender.clear();
        for (AbstractCard c : validCards.group) {
            if (colorFilter == null || c.color.equals(colorFilter)) {
                cardsToRender.addToTop(c);
            }
        }
        justSorted = true;
        calculateScrollBounds();
        if (!ignoreScrollReset) {
            currentDiffY = 0;
        }
        ignoreScrollReset = false;
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
            if (justSorted) {// 356
                cards.get(i).current_x = cards.get(i).target_x;
                cards.get(i).current_y = cards.get(i).target_y;
            }
            cards.get(i).update();// 349
            cards.get(i).updateHoverLogic();// 350
            if (cards.get(i).hb.hovered) {// 352
                this.hoveredCard = cards.get(i);// 353
            }
        }

        justSorted = false;
    }

    public void renderCards(SpriteBatch sb) {
        cardsToRender.renderInLibrary(sb);// 503
        cardsToRender.renderTip(sb);
        if (this.hoveredCard != null) {// 426
            this.hoveredCard.renderHoverShadow(sb);// 427
            this.hoveredCard.renderInLibrary(sb);// 428
        }
    }

    private void renderInfo(SpriteBatch sb) {
        FontHelper.renderFont(sb, FontHelper.tipBodyFont, TEXT[2]+selectedAugment.getModRarity().toString(), DROPDOWN_X, RARITY_Y, Settings.GOLD_COLOR);
        FontHelper.renderFont(sb, FontHelper.tipBodyFont, TEXT[3]+cardsToRender.size() + (colorFilter != null ? "/"+validCards.group.size() : ""), DROPDOWN_X, VALID_CARDS_Y, Settings.GOLD_COLOR);
    }

    private void renderUpgradeViewToggle(SpriteBatch sb) {
        FontHelper.cardTitleFont.getData().setScale(1.0F);
        sb.setColor(Color.WHITE);// 1712
        sb.draw(ImageMaster.CHECKBOX, this.upgradeHb.cX - 80.0F * Settings.scale - 32.0F, this.upgradeHb.cY - 32.0F, 32.0F, 32.0F, 64.0F, 64.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 64, 64, false, false);// 1713
        if (this.upgradeHb.hovered) {// 1731
            FontHelper.renderFont(sb, FontHelper.tipBodyFont, TEXT[1], this.upgradeHb.cX - 45.0F * Settings.scale, this.upgradeHb.cY + 10.0F * Settings.scale, Settings.BLUE_TEXT_COLOR);// 1732
        } else {
            FontHelper.renderFont(sb, FontHelper.tipBodyFont, TEXT[1], this.upgradeHb.cX - 45.0F * Settings.scale, this.upgradeHb.cY + 10.0F * Settings.scale, Settings.GOLD_COLOR);// 1740
        }

        if (upgradePreview) {// 1749
            sb.setColor(Color.WHITE);// 1750
            sb.draw(ImageMaster.TICK, this.upgradeHb.cX - 80.0F * Settings.scale - 32.0F, this.upgradeHb.cY - 32.0F, 32.0F, 32.0F, 64.0F, 64.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 64, 64, false, false);// 1751
        }

        this.upgradeHb.render(sb);// 1769
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

    private void calculateScrollBounds() {
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

    private void refreshDropdownMenu(DropdownMenu menu) {
        try {
            Object o = ReflectionHacks.getPrivate(menu, DropdownMenu.class, "selectionBox");
            ReflectionHacks.privateMethod(DropdownMenu.class, "changeSelectionToRow", Class.forName(DropdownMenu.class.getName()+"$DropdownRow")).invoke(menu, o);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @SpirePatch2(clz = AbstractCard.class, method = "renderInLibrary")
    public static class FixUpgrades {
        public static AbstractCard getCard(AbstractCard baseCard, AbstractCard copy) {
            if (CardCrawlGame.mainMenuScreen.screen == MainMenuPatches.Enums.MODIFIERS_VIEW) {
                return baseCard.makeStatEquivalentCopy();
            }
            return copy;
        }
        @SpireInstrumentPatch
        public static ExprEditor plz() {
            return new ExprEditor() {
                @Override
                public void edit(MethodCall m) throws CannotCompileException {
                    if (m.getClassName().equals(AbstractCard.class.getName()) && m.getMethodName().equals("makeCopy")) {
                        m.replace("$_ = "+ModifierScreen.FixUpgrades.class.getName()+".getCard($0, $proceed($$));");
                    }
                }
            };
        }
    }
}
