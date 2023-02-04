package CardAugments.ui;

import basemod.ModLabel;
import basemod.ModPanel;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.helpers.FontHelper;

import java.util.function.Consumer;

public class CenteredModLabel extends ModLabel {
    public CenteredModLabel(String labelText, float xPos, float yPos, ModPanel p, Consumer<ModLabel> updateFunc) {
        super(labelText, xPos, yPos, p, updateFunc);
    }

    public CenteredModLabel(String labelText, float xPos, float yPos, Color color, ModPanel p, Consumer<ModLabel> updateFunc) {
        super(labelText, xPos, yPos, color, p, updateFunc);
    }

    public CenteredModLabel(String labelText, float xPos, float yPos, BitmapFont font, ModPanel p, Consumer<ModLabel> updateFunc) {
        super(labelText, xPos, yPos, font, p, updateFunc);
    }

    public CenteredModLabel(String labelText, float xPos, float yPos, Color color, BitmapFont font, ModPanel p, Consumer<ModLabel> updateFunc) {
        super(labelText, xPos, yPos, color, font, p, updateFunc);
    }

    @Override
    public void render(SpriteBatch sb) {
        FontHelper.renderFontCentered(sb, this.font, this.text, this.x, this.y, this.color);
    }
}
