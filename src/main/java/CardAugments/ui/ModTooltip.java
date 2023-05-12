package CardAugments.ui;

import basemod.IUIElement;
import basemod.ModPanel;
import basemod.patches.com.megacrit.cardcrawl.helpers.TipHelper.HeaderlessTip;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;

import java.util.function.Consumer;

public class ModTooltip implements IUIElement {
    public Consumer<ModTooltip> updateFunc;
    public ModPanel parent;
    public String text;
    public float x;
    public float y;

    public ModTooltip(float xPos, float yPos, String tipText, ModPanel p, Consumer<ModTooltip> updateFunc) {
        this.text = tipText;// 34
        this.x = xPos * Settings.scale;// 35
        this.y = yPos * Settings.scale;// 36
        this.parent = p;
        this.updateFunc = updateFunc;
    }

    @Override
    public void render(SpriteBatch spriteBatch) {
        HeaderlessTip.renderHeaderlessTip(x + 60.0F * Settings.scale, y - 50.0F * Settings.scale, this.text);
    }

    @Override
    public void update() {
        updateFunc.accept(this);
    }

    @Override
    public int renderLayer() {
        return 0;
    }

    @Override
    public int updateOrder() {
        return 0;
    }

    public void set(float xPos, float yPos) {
        this.x = xPos * Settings.scale;// 64
        this.y = yPos * Settings.scale;// 65
    }// 66

    public void setX(float xPos) {
        this.x = xPos * Settings.scale;// 70
    }// 71

    public void setY(float yPos) {
        this.y = yPos * Settings.scale;// 75
    }// 76

    public float getX() {
        return this.x / Settings.scale;// 80
    }

    public float getY() {
        return this.y / Settings.scale;// 85
    }
}
