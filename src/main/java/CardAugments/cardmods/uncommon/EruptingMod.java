package CardAugments.cardmods.uncommon;

import CardAugments.CardAugmentsMod;
import CardAugments.cardmods.AbstractAugment;
import basemod.abstracts.AbstractCardModifier;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.actions.watcher.ChangeStanceAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.purple.*;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.stances.WrathStance;

import java.util.Arrays;
import java.util.List;

public class EruptingMod extends AbstractAugment {
    public static final String ID = CardAugmentsMod.makeID(EruptingMod.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;

    // Could argue to figure out what is a "stance card" programatically for mod compatibility, but it's not *that* important
    private static final List<Class<? extends AbstractCard>> CARD_BLACKLIST = Arrays.asList(
            Eruption.class,
            Crescendo.class,
            Indignation.class,
            //SimmeringFury.class,
            Tantrum.class,

            // One could argue to allow the non-Wrath cards to also be Erupting, switching stances twice instantly.
            Vigilance.class,
            Tranquility.class,
            FearNoEvil.class,
            InnerPeace.class,
            Meditate.class,

            Blasphemy.class,

            EmptyBody.class,
            EmptyFist.class,
            EmptyMind.class,

            Prostrate.class,
            Pray.class,
            Worship.class
            );

    @Override
    public void onInitialApplication(AbstractCard card) {
    }

    @Override
    public boolean validCard(AbstractCard card) {
        return card.color == AbstractCard.CardColor.PURPLE &&
                card.type != AbstractCard.CardType.POWER &&
                ! CARD_BLACKLIST.contains(card.getClass());
    }

    @Override
    public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
        addToBot(new ChangeStanceAction(WrathStance.STANCE_ID));
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
        return rawDescription + TEXT[2];
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.UNCOMMON;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new EruptingMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}
