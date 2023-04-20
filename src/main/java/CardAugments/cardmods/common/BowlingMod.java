package CardAugments.cardmods.common;

import CardAugments.CardAugmentsMod;
import CardAugments.cardmods.AbstractAugment;
import CardAugments.util.Wiz;
import basemod.abstracts.AbstractCardModifier;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class BowlingMod extends AbstractAugment {
    public static final String ID = CardAugmentsMod.makeID(BowlingMod.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;

    @Override
    public float modifyBaseDamage(float damage, DamageInfo.DamageType type, AbstractCard card, AbstractMonster target) {
        return damage * MAJOR_DEBUFF;
    }

    @Override
    public boolean validCard(AbstractCard card) {
        return cardCheck(card, c -> c.baseDamage > 1 && usesEnemyTargeting() && c.type == AbstractCard.CardType.ATTACK && customCheck(c, check -> noCardModDescriptionChanges(check) && check.rawDescription.chars().filter(ch -> ch == '.' || ch == '。').count() == 1));
    }

    @Override
    public String getPrefix() {
        return TEXT[0];
    }

    @Override
    public String getSuffix() {
        return TEXT[1];
    }

    @Override
    public String modifyDescription(String rawDescription, AbstractCard card) {
        return rawDescription.replaceFirst("[.。]", TEXT[2]);
    }

    @Override
    public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
        if (target instanceof AbstractMonster) {
            int hits = Wiz.getEnemies().size() - 1; //We already play the card once for the first monster
            for (int i = 0 ; i < hits ; i++) {
                card.use(Wiz.adp(), (AbstractMonster) target);
            }
            if (hits >= 2) { //As such only check for 2 additional hits to play sfx
                addToBot(new SFXAction("ATTACK_BOWLING"));
            }
        }
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.COMMON;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new BowlingMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}
