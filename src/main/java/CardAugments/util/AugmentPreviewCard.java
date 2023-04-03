package CardAugments.util;

import CardAugments.CardAugmentsMod;
import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireOverride;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class AugmentPreviewCard extends AbstractCard {
    public static final String[] MY_TEXT = CardCrawlGame.languagePack.getUIString(CardAugmentsMod.makeID("AugmentPreviewCard")).TEXT;
    private final Color typeColor = new Color(0.35F, 0.35F, 0.35F, 0.0F);
    private final Color renderColor;
    public AugmentPreviewCard(String name, String rawDescription) {
        super(CardAugmentsMod.makeID("PreviewCard"), name, "colorless/skill/insight", -2, rawDescription, CardType.SKILL, CardColor.COLORLESS, CardRarity.SPECIAL, CardTarget.NONE);
        renderColor = ReflectionHacks.getPrivateInherited(this, AugmentPreviewCard.class, "renderColor");
    }

    @Override
    public void upgrade() {}

    @Override
    public void use(AbstractPlayer abstractPlayer, AbstractMonster abstractMonster) {}


    @SpireOverride
    public void renderType(SpriteBatch sb) {
        String text = MY_TEXT[0];
        BitmapFont font = FontHelper.cardTypeFont;
        font.getData().setScale(this.drawScale);
        typeColor.a = renderColor.a;
        FontHelper.renderRotatedText(sb, font, text, current_x, current_y - 22.0F * drawScale * Settings.scale, 0.0F, -1.0F * drawScale * Settings.scale, angle, false, typeColor);
    }

    @Override
    public AbstractCard makeCopy() {
        return new AugmentPreviewCard(this.name, this.rawDescription);
    }
}
