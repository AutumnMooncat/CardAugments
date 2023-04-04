package CardAugments.ui;

import CardAugments.util.TextureLoader;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.controller.CInputActionSet;
import com.megacrit.cardcrawl.helpers.input.InputHelper;

public class SettingsButton {
    private static final int W = 512;
    private static final int H = 256;
    private static final Color HOVER_BLEND_COLOR = new Color(1.0F, 1.0F, 1.0F, 0.4F);
    private static final float SHOW_X = 256.0F * Settings.scale;
    private static final float DRAW_Y = 228.0F * Settings.scale;
    public static final float HIDE_X = SHOW_X - 400.0F * Settings.scale;
    public float current_x;
    private float target_x;
    public boolean isHidden;
    private float glowAlpha;
    private Color glowColor;
    private String buttonText;
    private static final float TEXT_OFFSET_X = -136.0F * Settings.scale;
    private static final float TEXT_OFFSET_Y = 57.0F * Settings.scale;
    private static final Texture BUTTON = TextureLoader.getTexture("CardAugmentsResources/images/ui/settingsButton.png");
    private static final Texture BUTTON_OUTLINE = TextureLoader.getTexture("CardAugmentsResources/images/ui/settingsButtonOutline.png");
    private static final Texture BUTTON_SHADOW = TextureLoader.getTexture("CardAugmentsResources/images/ui/settingsButtonShadow.png");
    public Hitbox hb;

    public SettingsButton() {
        this.current_x = HIDE_X;// 22
        this.target_x = this.current_x;// 23
        this.isHidden = true;// 24
        this.glowAlpha = 0.0F;// 25
        this.glowColor = Settings.GOLD_COLOR.cpy();// 26
        this.buttonText = "NOT_SET";// 29
        this.hb = new Hitbox(300.0F * Settings.scale, 100.0F * Settings.scale);// 34
        this.hb.move(SHOW_X - 106.0F * Settings.scale, DRAW_Y + 60.0F * Settings.scale);// 37
    }// 38

    public void update() {
        if (!this.isHidden) {// 41
            this.updateGlow();// 42
            this.hb.update();// 43
            if (InputHelper.justClickedLeft && this.hb.hovered) {// 44
                this.hb.clickStarted = true;// 45
                CardCrawlGame.sound.play("UI_CLICK_1");// 46
            }

            if (this.hb.justHovered) {// 48
                CardCrawlGame.sound.play("UI_HOVER");// 49
            }

            if (CInputActionSet.cancel.isJustPressed()) {// 52
                this.hb.clicked = true;// 53
            }
        }

        if (this.current_x != this.target_x) {// 56
            this.current_x = MathUtils.lerp(this.current_x, this.target_x, Gdx.graphics.getDeltaTime() * 9.0F);// 57
            if (Math.abs(this.current_x - this.target_x) < Settings.UI_SNAP_THRESHOLD) {// 58
                this.current_x = this.target_x;// 59
            }
        }

    }// 62

    private void updateGlow() {
        this.glowAlpha += Gdx.graphics.getDeltaTime() * 3.0F;// 65
        if (this.glowAlpha < 0.0F) {// 66
            this.glowAlpha *= -1.0F;// 67
        }

        float tmp = MathUtils.cos(this.glowAlpha);// 69
        if (tmp < 0.0F) {// 70
            this.glowColor.a = -tmp / 2.0F + 0.3F;// 71
        } else {
            this.glowColor.a = tmp / 2.0F + 0.3F;// 73
        }

    }// 75

    public boolean hovered() {
        return this.hb.hovered;// 78
    }

    public void hide() {
        if (!this.isHidden) {// 82
            this.hb.clicked = false;// 83
            this.hb.hovered = false;// 84
            InputHelper.justClickedLeft = false;// 85
            this.target_x = HIDE_X;// 86
            this.isHidden = true;// 87
        }

    }// 89

    public void hideInstantly() {
        if (!this.isHidden) {// 92
            this.hb.hovered = false;// 93
            InputHelper.justClickedLeft = false;// 94
            this.target_x = HIDE_X;// 95
            this.current_x = this.target_x;// 96
            this.isHidden = true;// 97
        }

    }// 99

    public void show(String buttonText) {
        if (this.isHidden) {// 102
            this.glowAlpha = 0.0F;// 103
            this.current_x = HIDE_X;// 104
            this.target_x = SHOW_X;// 105
            this.isHidden = false;// 106
            this.buttonText = buttonText;// 107
        } else {
            this.current_x = HIDE_X;// 109
            this.buttonText = buttonText;// 110
        }

        this.hb.hovered = false;// 112
    }// 113

    public void showInstantly(String buttonText) {
        this.current_x = SHOW_X;// 116
        this.target_x = SHOW_X;// 117
        this.isHidden = false;// 118
        this.buttonText = buttonText;// 119
        this.hb.hovered = false;// 120
    }// 121

    public void render(SpriteBatch sb) {
        sb.setColor(Color.WHITE);// 124
        this.renderShadow(sb);// 125
        sb.setColor(this.glowColor);// 126
        this.renderOutline(sb);// 127
        sb.setColor(Color.WHITE);// 128
        this.renderButton(sb);// 129
        if (this.hb.hovered && !this.hb.clickStarted) {// 131
            sb.setBlendFunction(770, 1);// 132
            sb.setColor(HOVER_BLEND_COLOR);// 133
            this.renderButton(sb);// 134
            sb.setBlendFunction(770, 771);// 135
        }

        Color tmpColor = Settings.LIGHT_YELLOW_COLOR;// 138
        if (this.hb.clickStarted) {// 139
            tmpColor = Color.LIGHT_GRAY;// 140
        }

        if (Settings.isControllerMode) {// 142
            FontHelper.renderFontLeft(sb, FontHelper.buttonLabelFont, this.buttonText, this.current_x + TEXT_OFFSET_X - 30.0F * Settings.scale, DRAW_Y + TEXT_OFFSET_Y, tmpColor);// 143
        } else {
            FontHelper.renderFontCentered(sb, FontHelper.buttonLabelFont, this.buttonText, this.current_x + TEXT_OFFSET_X, DRAW_Y + TEXT_OFFSET_Y, tmpColor);// 151
        }

        this.renderControllerUi(sb);// 160
        if (!this.isHidden) {// 162
            this.hb.render(sb);// 163
        }

    }// 165

    private void renderShadow(SpriteBatch sb) {
        sb.draw(BUTTON_SHADOW, this.current_x - 256.0F, DRAW_Y - 128.0F, 256.0F, 128.0F, 512.0F, 256.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 512, 256, false, false);// 168
    }// 185

    private void renderOutline(SpriteBatch sb) {
        sb.draw(BUTTON_OUTLINE, this.current_x - 256.0F, DRAW_Y - 128.0F, 256.0F, 128.0F, 512.0F, 256.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 512, 256, false, false);// 188
    }// 205

    private void renderButton(SpriteBatch sb) {
        sb.draw(BUTTON, this.current_x - 256.0F, DRAW_Y - 128.0F, 256.0F, 128.0F, 512.0F, 256.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 512, 256, false, false);// 208
    }// 225

    private void renderControllerUi(SpriteBatch sb) {
        if (Settings.isControllerMode) {// 228
            sb.setColor(Color.WHITE);// 229
            sb.draw(CInputActionSet.cancel.getKeyImg(), this.current_x - 32.0F - 210.0F * Settings.scale, DRAW_Y - 32.0F + 57.0F * Settings.scale, 32.0F, 32.0F, 64.0F, 64.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 64, 64, false, false);// 230 231
        }

    }// 248
}

