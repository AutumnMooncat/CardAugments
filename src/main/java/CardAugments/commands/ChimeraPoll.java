package CardAugments.commands;

import CardAugments.CardAugmentsMod;
import CardAugments.cardmods.AbstractAugment;
import basemod.DevConsole;
import basemod.devcommands.ConsoleCommand;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.helpers.CardLibrary;

import java.util.ArrayList;
import java.util.Collections;

public class ChimeraPoll extends ConsoleCommand {
    public ChimeraPoll() {
        requiresPlayer = true;
        minExtraTokens = 1;
        maxExtraTokens = 1;
    }

    @Override
    public void execute(String[] tokens, int depth) {
        if (CardAugmentsMod.modMap.containsKey(tokens[depth])) {
            AbstractAugment a = CardAugmentsMod.modMap.get(tokens[depth]);
            ArrayList<String> validCards = Chimera.getAllValidCards(a);
            Collections.sort(validCards);
            DevConsole.log(validCards.size()+" valid cards found. Dumping to logger.");
            CardAugmentsMod.logger.info(validCards.size()+" valid cards found for modifier "+a.identifier(null));
            CardAugmentsMod.logger.info(validCards);
            /*if (validCards.size() >= 30) {
                DevConsole.log(validCards.size()+" valid cards found. Dumping to logger.");
                CardAugmentsMod.logger.info(validCards.size()+" valid cards found for modifier "+a.identifier(null));
                CardAugmentsMod.logger.info(validCards);
            } else {
                String ret = validCards.size()+" valid cards found, listing: ";
                for (String s : validCards) {
                    ret += " "+s;
                    if (ret.length() > 100) {
                        DevConsole.log(ret);
                        ret = "";
                    }
                }
                if (!ret.isEmpty()) {
                    DevConsole.log(ret);
                }
            }*/
        } else {
            DevConsole.log("could not find mod " + tokens[depth]);
        }
    }

    @Override
    public ArrayList<String> extraOptions(String[] tokens, int depth) {
        ArrayList<String> options = Chimera.getAllMods();
        if(options.contains(tokens[depth])) {
            options.clear();
        } else if(tokens.length > depth + 1) {
            tooManyTokensError();
        }
        return options;
    }

    @Override
    public void errorMsg() {
        cmdChimeraHelp();
    }

    public static void cmdChimeraHelp() {
        DevConsole.couldNotParse();
        DevConsole.log("options are:");
        DevConsole.log("* * [mod id]");
        DevConsole.log("* * [mod id]");
    }
}