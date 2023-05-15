package CardAugments.commands;

import CardAugments.CardAugmentsMod;
import CardAugments.cardmods.AbstractAugment;
import CardAugments.patches.RolledModFieldPatches;
import basemod.DevConsole;
import basemod.devcommands.ConsoleCommand;
import basemod.helpers.CardModifierManager;
import basemod.helpers.ConvertHelper;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;

import java.util.ArrayList;

public class ChimeraDeckAdd extends ConsoleCommand {
    public ChimeraDeckAdd() {
        requiresPlayer = true;
        minExtraTokens = 2;
        maxExtraTokens = 4;
    }

    @Override
    public void execute(String[] tokens, int depth) {
        if (CardAugmentsMod.modMap.containsKey(tokens[depth+1])) {
            String cardName = Chimera.unpackCardName(tokens[depth]);
            AbstractCard c = CardLibrary.getCard(cardName);
            AbstractAugment a = CardAugmentsMod.modMap.get(tokens[depth+1]);
            if (c != null) {
                if (a.canApplyTo(c)) {
                    int count = 1;
                    if (tokens.length > depth + 2 && ConvertHelper.tryParseInt(tokens[depth + 2]) != null) {
                        count = ConvertHelper.tryParseInt(tokens[depth + 2], 0);
                    }

                    int upgradeCount = 0;
                    if (tokens.length > depth + 3 && ConvertHelper.tryParseInt(tokens[depth + 3]) != null) {
                        upgradeCount = ConvertHelper.tryParseInt(tokens[depth + 3], 0);
                    }
                    DevConsole.log("adding " + count + (count == 1 ? " copy of " : " copies of ") + cardName + " with " + a.getClass().getSimpleName() + " and " + upgradeCount + " upgrade(s)");
                    for (int i = 0 ; i < count ; i++) {
                        AbstractCard copy = c.makeCopy();
                        CardModifierManager.addModifier(copy, a.makeCopy());
                        for (int j = 0 ; j < upgradeCount ; j++) {
                            copy.upgrade();
                        }
                        UnlockTracker.markCardAsSeen(copy.cardID);
                        RolledModFieldPatches.RolledModField.rolled.set(copy, true);
                        AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(copy, (float) Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F));
                    }
                } else {
                    DevConsole.log(a.getClass().getSimpleName() + " cannot be applied to " + cardName);
                }
            } else {
                DevConsole.log("could not find card " + cardName);// 48
            }
        }
        else {
            DevConsole.log("could not find mod " + tokens[depth+1]);
        }
    }

    @Override
    public ArrayList<String> extraOptions(String[] tokens, int depth) {
        ArrayList<String> options = ConsoleCommand.getCardOptions();
        if(options.contains(tokens[depth])) { //Input cardID is correct
            options.clear();
            String cardName = Chimera.unpackCardName(tokens[depth]);
            AbstractCard c = CardLibrary.getCard(cardName);
            //AbstractCard c = CardLibrary.getCard(Chimera.cardName(tokens).trim());
            if (c != null) {
                options = Chimera.getValidMods(c);
                if (tokens.length > depth + 1) {
                    if (options.contains(tokens[depth+1])) {
                        if (tokens.length > depth + 2 && tokens[depth + 2].matches("\\d*")) {// 56
                            if (tokens.length > depth + 3) {// 57
                                if (tokens[depth + 3].matches("\\d+")) {// 58
                                    ConsoleCommand.complete = true;// 59
                                } else if (tokens[depth + 3].length() > 0) {// 60
                                    tooManyTokensError();// 61
                                }
                            }
                            return ConsoleCommand.smallNumbers();// 64
                        }
                    }
                }
            } else {
                tooManyTokensError();
            }
        } else if(tokens.length > depth + 1) {//CardID is not correct, but you're typing more parameters???
            tooManyTokensError();
        }
        return options;
    }

    @Override
    public void errorMsg() {
        Chimera.cmdChimeraHelp();
    }
}