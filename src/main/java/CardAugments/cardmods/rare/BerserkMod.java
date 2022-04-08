package CardAugments.cardmods.rare;

import CardAugments.CardAugmentsMod;
import CardAugments.cardmods.AbstractAugment;
import CardAugments.cardmods.common.SlayerMod;
import basemod.abstracts.AbstractCardModifier;
import com.badlogic.gdx.Gdx;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.ExhaustEmberEffect;
import com.megacrit.cardcrawl.vfx.combat.FlameParticleEffect;

public class BerserkMod extends AbstractAugment {
    public static final String ID = CardAugmentsMod.makeID("BerserkMod");
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;
    private static final float VFX_RATE = 0.1F;
    private float vfxTimer = 0F;

    @Override
    public boolean validCard(AbstractCard card) {
        return card.baseDamage > 0 && card.type == AbstractCard.CardType.ATTACK;
    }

    @Override
    public String modifyName(String cardName, AbstractCard card) {
        return TEXT[0] + cardName + TEXT[1];
    }

    @Override
    public float modifyDamageFinal(float damage, DamageInfo.DamageType type, AbstractCard card, AbstractMonster target) {
        if (AbstractDungeon.player.isBloodied) {
            return damage * 1.5F;
        }
        return damage;
    }

    @Override
    public void onUpdate(AbstractCard card) {
        if (AbstractDungeon.player.isBloodied && AbstractDungeon.player.hand.contains(card)) {
            vfxTimer += Gdx.graphics.getDeltaTime();
            if (vfxTimer >= VFX_RATE) {
                vfxTimer = 0;
                AbstractDungeon.effectsQueue.add(new FlameParticleEffect(card.current_x, card.current_y+card.hb.height/3F));
                AbstractDungeon.effectsQueue.add(new ExhaustEmberEffect(card.current_x, card.current_y+card.hb.height/3F));
            }
        }
    }

    @Override
    public String modifyDescription(String rawDescription, AbstractCard card) {
        return rawDescription + TEXT[2];
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.RARE;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new BerserkMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }

}
