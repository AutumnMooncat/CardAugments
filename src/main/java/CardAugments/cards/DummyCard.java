package CardAugments.cards;

import CardAugments.CardAugmentsMod;
import basemod.abstracts.CustomCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class DummyCard extends CustomCard {
    public static final String ID = CardAugmentsMod.makeID("DummyCard");

    public DummyCard() {
        super(ID, "Name", "images/cards/locked_attack.png", 1, "Description", CardType.ATTACK, CardColor.COLORLESS, CardRarity.SPECIAL, CardTarget.ALL);
        baseDamage = damage = 1;
        baseBlock = block = 1;
        baseMagicNumber = magicNumber = 1;
    }

    public void setMultiDamage(boolean var) {
        this.isMultiDamage = var;
    }

    @Override
    public void upgrade() {}

    @Override
    public void use(AbstractPlayer abstractPlayer, AbstractMonster abstractMonster) {}
}
