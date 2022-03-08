package CardAugments.util;

import basemod.Pair;
import basemod.ReflectionHacks;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.evacipated.cardcrawl.modthespire.lib.SpireInstrumentPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.helpers.ShaderHelper;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

import java.util.HashMap;

import static com.badlogic.gdx.graphics.GL20.GL_DST_COLOR;
import static com.badlogic.gdx.graphics.GL20.GL_ZERO;

public class PortraitHelper {

    private static final Texture attackMask = TextureLoader.getTexture("CardAugmentsResources/images/cards/AttackMask.png");
    private static final Texture skillMask = TextureLoader.getTexture("CardAugmentsResources/images/cards/SkillMask.png");
    private static final Texture powerMask = TextureLoader.getTexture("CardAugmentsResources/images/cards/PowerMask.png");
    private static final int WIDTH = 250;
    private static final int HEIGHT = 190;
    private static final HashMap<Pair<String, AbstractCard.CardType>, Pair<TextureAtlas.AtlasRegion, Texture>> hashedTextures = new HashMap<>();

//    private static final ShaderProgram blurShader;

//    static {
//        blurShader = new ShaderProgram(Gdx.files.internal("shaders/blur/vertexShader.vs"), Gdx.files.internal("shaders/blur/fragShader.fs"));
//        blurShader.setUniformf("dir", 1f, 0f);
//        blurShader.setUniformf("resolution", 250f);
//        blurShader.setUniformf("radius", 4);
//    }


    public static void setMaskedPortrait(AbstractCard card) {
        Pair<String, AbstractCard.CardType> key = new Pair<>(card.cardID, card.type);
        if (hashedTextures.containsKey(key)) {
            card.portrait = hashedTextures.get(key).getKey();
        } else {
            Texture temp = makeMaskedTexture(card, 2);
            card.portrait = new TextureAtlas.AtlasRegion(makeMaskedTexture(card, 1), 0, 0, WIDTH, HEIGHT);
            hashedTextures.put(key, new Pair<>(card.portrait, temp));
        }
    }

    public static Texture makeMaskedTexture(AbstractCard card, int multi) {
        int width = WIDTH * multi;
        int height = HEIGHT * multi;

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
            case STATUS:
            case CURSE:
                spriteBatch.draw(skillMask, 0, height, width, -height);
                break;
            case POWER:
                spriteBatch.draw(powerMask, 0, height, width, -height);
                break;
        }
        spriteBatch.end();
        frameBuffer.end();
        return frameBuffer.getColorBufferTexture();
    }

    @SpirePatch2(clz = SingleCardViewPopup.class, method = "loadPortraitImg")
    public static class FixSCVHopefully {
        @SpirePostfixPatch
        public static void dontExplode(SingleCardViewPopup __instance, @ByRef Texture[] ___portraitImg, AbstractCard ___card) {
            Pair<String, AbstractCard.CardType> key = new Pair<>(___card.cardID, ___card.type);
            if (hashedTextures.containsKey(key)) {
                ___portraitImg[0] = hashedTextures.get(key).getValue();
            }
        }
    }

    public static boolean checkHash(SingleCardViewPopup scv) {
        AbstractCard c = ReflectionHacks.getPrivate(scv, SingleCardViewPopup.class, "card");
        Pair<String, AbstractCard.CardType> key = new Pair<>(c.cardID, c.type);
        return !hashedTextures.containsKey(key);
    }

    @SpirePatch2(clz = SingleCardViewPopup.class, method = "close")
    @SpirePatch2(clz = SingleCardViewPopup.class, method = "updateBetaArtToggler")
    public static class StopDisposingMyHashedImages {
        @SpireInstrumentPatch
        public static ExprEditor patch() {
            return new ExprEditor() {
                @Override
                //Method call is basically the equivalent of a methodcallmatcher of an insert patch, checks the edit method against every method call in the function you#re patching
                public void edit(MethodCall m) throws CannotCompileException {
                    //If the method is from the class AnimationState and the method is called update
                    if (m.getClassName().equals(Texture.class.getName()) && m.getMethodName().equals("dispose")) {
                        m.replace("if(CardAugments.util.PortraitHelper.checkHash(this)) {$proceed($$);}");
                    }
                }
            };
        }
    }
}