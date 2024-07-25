package CardAugments.util;

import basemod.Pair;
import basemod.abstracts.CustomCard;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;

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

    public static void setMaskedPortrait(AbstractCard card) {
        if (CardLibrary.getCard(card.cardID) == null) {
            return;
        }
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

        AbstractCard baseCard = CardLibrary.getCard(card.cardID).makeCopy();
        TextureAtlas.AtlasRegion t = baseCard.portrait;
        FrameBuffer fb = new FrameBuffer(Pixmap.Format.RGBA8888, width, height, false);
        SpriteBatch sb = new SpriteBatch();
        OrthographicCamera og = new OrthographicCamera(width, height);
        t.flip(false, true);
        if (baseCard.type == AbstractCard.CardType.ATTACK) {
            if (card.type == AbstractCard.CardType.POWER) {
                //Attack to Power
                og.zoom = 0.976f;
                og.translate(-3, 0);
            } else {
                //Attack to Skill, Status, Curse
                og.zoom = 0.9f;
                og.translate(0, -10);
            }
        } else if (baseCard.type == AbstractCard.CardType.POWER) {
            if (card.type == AbstractCard.CardType.ATTACK) {
                //Power to Attack
                og.zoom = 0.9f;
                og.translate(0, -10);
            } else {
                //Power to Skill, Status, Curse
                og.zoom = 0.825f;
                og.translate(-1, -18);
            }
        } else {
            if (card.type == AbstractCard.CardType.POWER) {
                //Skill, Status, Curse to Power
                og.zoom = 0.976f;
                og.translate(-3, 0);
            }
            //Skill, Status, Curse to Attack is free
        }

        og.update();
        sb.setProjectionMatrix(og.combined);

        ImageHelper.beginBuffer(fb);
        sb.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        fb.begin();
        sb.begin();

        sb.setColor(Color.WHITE.cpy());
        sb.draw(t, -width/2f, -height/2f, -width/2f, -height/2f, width, height, 1, 1, 0);
        sb.draw(t, -t.packedWidth/2f*multi, -t.packedHeight/2f*multi, -t.packedWidth/2f*multi, -t.packedHeight/2f*multi, t.packedWidth*multi, t.packedHeight*multi, 1, 1, 0);
        sb.setBlendFunction(GL_DST_COLOR, GL_ZERO);
        sb.setProjectionMatrix(new OrthographicCamera(width, height).combined);

        Texture mask = skillMask;
        if (card.type == AbstractCard.CardType.ATTACK) {
            mask = attackMask;
        } else if (card.type == AbstractCard.CardType.POWER) {
            mask = powerMask;
        }
        sb.draw(mask, -width/2f, -height/2f, -width/2f, -height/2f, width, height, 1, 1, 0, 0, 0, mask.getWidth(), mask.getHeight(), false, true);

        sb.end();
        fb.end();
        t.flip(false, true);
        return fb.getColorBufferTexture();
    }

    @SpirePatch2(clz = SingleCardViewPopup.class, method = "loadPortraitImg")
    public static class FixSCVHopefully {
        @SpirePostfixPatch
        public static void dontExplode(SingleCardViewPopup __instance, @ByRef Texture[] ___portraitImg, AbstractCard ___card) {
            Pair<String, AbstractCard.CardType> key = new Pair<>(___card.cardID, ___card.type);
            if (hashedTextures.containsKey(key)) {
                ___portraitImg[0] = makeMaskedTexture(___card, 2);
            }
        }
    }

    @SpirePatch2(clz = CustomCard.class, method = "getPortraitImage",paramtypez = {})
    public static class FixCustomCardHopefully {
        @SpirePostfixPatch
        public static void plz(CustomCard __instance, @ByRef Texture[] __result) {
            Pair<String, AbstractCard.CardType> key = new Pair<>(__instance.cardID, __instance.type);
            if (hashedTextures.containsKey(key)) {
                __result[0] = makeMaskedTexture(__instance, 2);
            }
        }
    }
}