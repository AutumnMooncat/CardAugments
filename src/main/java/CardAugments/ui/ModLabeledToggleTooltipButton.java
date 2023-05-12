package CardAugments.ui;

import basemod.ModLabeledToggleButton;
import basemod.ModPanel;
import basemod.ModToggleButton;
import basemod.patches.com.megacrit.cardcrawl.helpers.TipHelper.HeaderlessTip;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;

import java.util.function.Consumer;

public class ModLabeledToggleTooltipButton extends ModLabeledToggleButton {
    private final Consumer<ModLabeledToggleTooltipButton> updateFunc;
    public ModLabeledToggleTooltipButton(String labelText, String tooltipText, float xPos, float yPos, Color color, BitmapFont font, boolean enabled, ModPanel p, Consumer<ModLabeledToggleTooltipButton> updateFunc, Consumer<ModToggleButton> c) {
        super(labelText, tooltipText, xPos, yPos, color, font, enabled, p, l -> {}, c);
        this.updateFunc = updateFunc;
    }

    @Override
    public void render(SpriteBatch sb) {
        this.toggle.render(sb);// 41
        this.text.render(sb);// 42
        if (this.toggle.enabled) {// 44
            HeaderlessTip.renderHeaderlessTip(toggle.getX()*Settings.scale + 00.0F*Settings.scale, toggle.getY()*Settings.scale - 35.0F*Settings.scale, this.tooltip);
        }
    }

    @Override
    public void update() {
        updateFunc.accept(this);
        super.update();
    }
}
