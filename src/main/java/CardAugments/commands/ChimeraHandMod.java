package CardAugments.commands;

import CardAugments.CardAugmentsMod;
import CardAugments.cardmods.AbstractAugment;
import basemod.devcommands.ConsoleCommand;
import basemod.helpers.CardModifierManager;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;

import java.util.ArrayList;

public class ChimeraHandMod extends ConsoleCommand {
    public ChimeraHandMod() {
        requiresPlayer = true;
        minExtraTokens = 2;
        maxExtraTokens = 2;
    }

    @Override
    public void execute(String[] tokens, int depth) {
        if (CardAugmentsMod.modMap.containsKey(tokens[depth+1])) {
            String cardName = Chimera.unpackCardName(tokens[depth]);
            AbstractAugment a = CardAugmentsMod.modMap.get(tokens[depth+1]);
            for (AbstractCard c : AbstractDungeon.player.hand.group) {
                if (c.cardID.equals(cardName) && a.canApplyTo(c)) {
                    CardModifierManager.addModifier(c, a.makeCopy());
                    break;
                }
            }
        }
        else {
            Chimera.cmdChimeraHelp();
        }
    }

    @Override
    public ArrayList<String> extraOptions(String[] tokens, int depth) {
        ArrayList<String> options = ConsoleCommand.getCardOptionsFromCardGroup(AbstractDungeon.player.hand);
        if(options.contains(tokens[depth])) { //Input cardID is correct
            options.clear();
            String cardName = Chimera.unpackCardName(tokens[depth]);
            AbstractCard c = CardLibrary.getCard(cardName);
            if (c != null) {
                options = Chimera.getValidMods(c);
                if (tokens.length > depth + 1) {
                    if (options.contains(tokens[depth+1])) {
                        ConsoleCommand.complete = true;
                    }
                } else if (tokens.length > depth + 2) {
                    tooManyTokensError();
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