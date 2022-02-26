package CardAugments.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.helpers.ShaderHelper;

import static com.badlogic.gdx.graphics.GL20.GL_DST_COLOR;
import static com.badlogic.gdx.graphics.GL20.GL_ZERO;

public class PortraitHelper {

    private static final Texture attackMask = TextureLoader.getTexture("CardAugmentsResources/images/cards/AttackMask.png");
    private static final Texture skillMask = TextureLoader.getTexture("CardAugmentsResources/images/cards/SkillMask.png");
    private static final Texture powerMask = TextureLoader.getTexture("CardAugmentsResources/images/cards/PowerMask.png");

//    private static final ShaderProgram blurShader;

//    static {
//        blurShader = new ShaderProgram(Gdx.files.internal("shaders/blur/vertexShader.vs"), Gdx.files.internal("shaders/blur/fragShader.fs"));
//        blurShader.setUniformf("dir", 1f, 0f);
//        blurShader.setUniformf("resolution", 250f);
//        blurShader.setUniformf("radius", 4);
//    }


    public static void setMaskedPortrait(AbstractCard card) {
        int width = 250;
        int height = 190;

        FrameBuffer frameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, width, height, false);
        SpriteBatch spriteBatch = new SpriteBatch();
        Matrix4 matrix = new Matrix4();
        matrix.setToOrtho2D(0, 0, width, height);
        spriteBatch.setProjectionMatrix(matrix);
        spriteBatch.setBlendFunction(770, 771);
        frameBuffer.begin();
        spriteBatch.begin();
        spriteBatch.setColor(Color.WHITE.cpy());

        // the draws are all upside-down because when the frameBuffer writes it flips (for some reason)
        float bgScale = 0.3f;
//        spriteBatch.setShader(blurShader);  // TODO: get a blur shader to work
        spriteBatch.draw(card.portrait, bgScale/2 * -width, (1+bgScale/2)*height, (1+bgScale)*width, (1+bgScale)*-height);

//        ShaderHelper.setShader(spriteBatch, ShaderHelper.Shader.DEFAULT);
        spriteBatch.draw(card.portrait, 0, height, width, -height);

        spriteBatch.setBlendFunction(GL_DST_COLOR, GL_ZERO);

        switch (card.type) {
            case ATTACK:
                spriteBatch.draw(attackMask, 0, height, width, -height);
                break;
            case SKILL:
                spriteBatch.draw(skillMask, 0, height, width, -height);
                break;
            case POWER:
                spriteBatch.draw(powerMask, 0, height, width, -height);
                break;
        }
        spriteBatch.end();
        frameBuffer.end();
        // TODO: also set the scv (large) portrait
        card.portrait = new TextureAtlas.AtlasRegion(frameBuffer.getColorBufferTexture(), 0, 0, width, height);
    }
}