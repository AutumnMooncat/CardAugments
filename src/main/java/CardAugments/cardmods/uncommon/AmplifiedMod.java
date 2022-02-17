package CardAugments.cardmods.uncommon;

import CardAugments.CardAugmentsMod;
import CardAugments.cardmods.AbstractAugment;
import basemod.abstracts.AbstractCardModifier;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.colorless.PanicButton;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import javassist.ClassPool;
import javassist.CtMethod;
import javassist.expr.ExprEditor;
import javassist.expr.FieldAccess;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;

public class AmplifiedMod extends AbstractAugment {
    private static final ArrayList<String> excluded = new ArrayList<>(Arrays.asList(PanicButton.ID));
    public static final String ID = CardAugmentsMod.makeID("AmplifiedMod");
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;

    @Override
    public void onInitialApplication(AbstractCard card) {
        modifyBaseStat(card, BuffType.MAGIC, BuffScale.MODERATE_BUFF);
        card.cost = card.cost + 1;
        card.costForTurn = card.cost;
    }

    @Override
    public boolean shouldApply(AbstractCard card) {
        AbstractCard upgradeCheck = card.makeCopy();
        upgradeCheck.upgrade();
        return card.cost == upgradeCheck.cost && card.baseMagicNumber <= upgradeCheck.baseMagicNumber && validCard(card);
    }

    @Override
    public boolean validCard(AbstractCard card) {
        return card.cost >= 0 && usesMagic(card);
    }

    private static boolean usesMagic;

    private boolean usesMagic(AbstractCard card) {
        usesMagic = false;
        if (card.baseMagicNumber > 0 && StringUtils.containsIgnoreCase(card.rawDescription, "!M!") && excluded.stream().noneMatch(str -> str.equals(card.cardID))) {
            try {
                ClassPool pool = Loader.getClassPool();
                CtMethod ctClass = pool.get(card.getClass().getName()).getDeclaredMethod("use");

                ctClass.instrument(new ExprEditor() {
                    @Override
                    public void edit(FieldAccess f) {

                        if (f.getFieldName().equals("magicNumber") && !f.isWriter()) {
                            usesMagic = true;
                        }

                    }
                });

            } catch (Exception ignored) { }
        }
        return usesMagic;
    }

    @Override
    public String modifyName(String cardName, AbstractCard card) {
        return TEXT[0] + cardName + TEXT[1];
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.UNCOMMON;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new AmplifiedMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}
