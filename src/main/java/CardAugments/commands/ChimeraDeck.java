package CardAugments.commands;

import basemod.DevConsole;
import basemod.devcommands.ConsoleCommand;

public class ChimeraDeck extends ConsoleCommand {
    public ChimeraDeck() {
        followup.put("mod", ChimeraDeckMod.class);
        followup.put("add", ChimeraDeckAdd.class);
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

    public static void cmdChimeraHelp() {
        DevConsole.couldNotParse();
        DevConsole.log("options are:");
        DevConsole.log("* * mod [card id] [mod id]");
        DevConsole.log("* * add [card id] [mod id]");
    }
}