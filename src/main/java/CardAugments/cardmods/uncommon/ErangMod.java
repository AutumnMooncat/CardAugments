package CardAugments.cardmods.uncommon;

import CardAugments.CardAugmentsMod;
import CardAugments.cardmods.AbstractAugment;
import CardAugments.patches.InterruptUseCardFieldPatches;
import CardAugments.util.Wiz;
import basemod.abstracts.AbstractCardModifier;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class ErangMod extends AbstractAugment {
    public static final String ID = CardAugmentsMod.makeID(ErangMod.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;
    public static final String[] CARD_TEXT = CardCrawlGame.languagePack.getUIString(ID).EXTRA_TEXT;

    @Override
    public void onInitialApplication(AbstractCard card) {
        InterruptUseCardFieldPatches.InterceptUseField.interceptUse.set(card, true);
        card.target = AbstractCard.CardTarget.ALL_ENEMY;
    }

    @Override
    public float modifyBaseDamage(float damage, DamageInfo.DamageType type, AbstractCard card, AbstractMonster target) {
        return damage * HUGE_DEBUFF;
    }

    @Override
    public boolean validCard(AbstractCard card) {
        return cardCheck(card, c -> c.baseDamage > 1 && usesEnemyTargeting() && c.type == AbstractCard.CardType.ATTACK && customCheck(c, check -> noCardModDescriptionChanges(check) && check.rawDescription.chars().filter(ch -> ch == '.' || ch == '。').count() == 1));
    }

    @Override
    public String modifyName(String cardName, AbstractCard card) {
        String[] nameParts = removeUpgradeText(cardName);
        if (nameParts[0].equals(TEXT[3])) {
            return nameParts[0] + TEXT[4] + nameParts[1];
        }
        if (nameParts[0].endsWith(TEXT[0].substring(0, 2))) {
            return nameParts[0] + TEXT[2] + nameParts[1];
        } else if (nameParts[0].endsWith(TEXT[0].substring(0, 1))) {
            return nameParts[0] + TEXT[1] + nameParts[1];
        }
        return nameParts[0] + TEXT[0] + nameParts[1];
    }

    @Override
    public String getAugmentDescription() {
        return TEXT[5];
    }

    @Override
    public String modifyDescription(String rawDescription, AbstractCard card) {
        return rawDescription.replaceFirst("[.。]", CARD_TEXT[0]);
    }

    @Override
    public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
        for (int i = 0 ; i < 3 ; i++) {
            Wiz.atb(new AbstractGameAction() {
                @Override
                public void update() {
                    AbstractMonster m = AbstractDungeon.getRandomMonster();
                    if (m != null) {
                        card.use(AbstractDungeon.player, m);
                    }
                    this.isDone = true;
                }
            });
        }
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.UNCOMMON;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new ErangMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}
