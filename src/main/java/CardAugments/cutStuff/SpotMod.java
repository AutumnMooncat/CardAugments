/*
package CardAugments.cutStuff;

import CardAugments.CardAugmentsMod;
import CardAugments.cardmods.AbstractAugment;
import CardAugments.patches.InterruptUseCardFieldPatches;
import basemod.abstracts.AbstractCardModifier;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.ThoughtBubble;

public class SpotMod extends AbstractAugment {
    public static final String ID = CardAugmentsMod.makeID(SpotMod.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;

    @Override
    public void onInitialApplication(AbstractCard card) {
        card.exhaust = false;
        if (card.target == AbstractCard.CardTarget.SELF)
            card.target = AbstractCard.CardTarget.SELF_AND_ENEMY;
        if (card.target == AbstractCard.CardTarget.NONE)
            card.target = AbstractCard.CardTarget.ENEMY;
        InterruptUseCardFieldPatches.InterceptUseField.interceptUse.set(card, true);
    }

    @Override
    public boolean validCard(AbstractCard card) {
        return card.exhaust &&
                card.target != AbstractCard.CardTarget.ALL &&
                card.target != AbstractCard.CardTarget.ALL_ENEMY &&
                ! card.tags.contains(AbstractCard.CardTags.HEALING);
    }

    @Override
    public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
        if ( !(target instanceof AbstractMonster && ((AbstractMonster)target).getIntentBaseDmg() >= 0)) {
            AbstractDungeon.effectList.add(new ThoughtBubble(AbstractDungeon.player.dialogX, AbstractDungeon.player.dialogY, 3.0F, TEXT[5], true));
        } else {
            card.use(AbstractDungeon.player, (AbstractMonster) target);
        }
    }

    @Override
    public String getPrefix() {
        return TEXT[0];
    }

    @Override
    public String getSufix() {
        return TEXT[1];
    }

    @Override
    public String modifyDescription(String rawDescription, AbstractCard card) {
        rawDescription = rawDescription.replace(TEXT[3], TEXT[4]);
        return TEXT[2] + rawDescription;
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.UNCOMMON;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new SpotMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}

  "CardAugments:SpotMod": {
    "TEXT": ["Spot ", "", "If the enemy intends to attack: NL ",
      " NL Exhaust.", "",
      "That target does not intend to attack."]
  },
*/
