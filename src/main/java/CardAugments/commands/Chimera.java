package CardAugments.commands;

import CardAugments.CardAugmentsMod;
import CardAugments.cardmods.AbstractAugment;
import basemod.BaseMod;
import basemod.DevConsole;
import basemod.devcommands.ConsoleCommand;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.helpers.CardLibrary;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class Chimera extends ConsoleCommand {
    public Chimera() {
        followup.put("hand", ChimeraHand.class);
        followup.put("deck", ChimeraDeck.class);
        followup.put("poll", ChimeraPoll.class);
        requiresPlayer = true;
        simpleCheck = true;
    }

    @Override
    public void execute(String[] tokens, int depth) {
        cmdChimeraHelp();
    }

    @Override
    public void errorMsg() {
        cmdChimeraHelp();
    }

    public static String unpackCardName(String cardName) {
        if (BaseMod.underScoreCardIDs.containsKey(cardName)) {
            cardName = BaseMod.underScoreCardIDs.get(cardName);
        }
        return cardName;
    }

    public static ArrayList<String> getValidMods(AbstractCard card) {
        return CardAugmentsMod.modMap.keySet().stream().filter(s -> CardAugmentsMod.modMap.get(s).validCard(card)).collect(Collectors.toCollection(ArrayList::new));
    }

    public static ArrayList<String> getAllMods() {
        return new ArrayList<>(CardAugmentsMod.modMap.keySet());
    }

    public static ArrayList<String> getAllValidCards(AbstractAugment a) {
        ArrayList<String> cardIDs = new ArrayList<>();
        for (String s : CardLibrary.cards.keySet()) {
            if (a.validCard(CardLibrary.cards.get(s))) {
                String cardid = CardLibrary.cards.get(s).cardID.replace(' ', '_');
                if (!cardIDs.contains(cardid)) {// 248
                    cardIDs.add(cardid);
                }
            }
        }
        return cardIDs;
    }

    public static void cmdChimeraHelp() {
        DevConsole.couldNotParse();
        DevConsole.log("options are:");
        DevConsole.log("* hand mod [card id] [mod id]");
        DevConsole.log("* hand add [card id] [mod id] {count} {upgrade amt}");
        DevConsole.log("* deck mod [card id] [mod id]");
        DevConsole.log("* deck add [card id] [mod id] {count} {upgrade amt}");
        DevConsole.log("* poll [mod id]");
    }
}