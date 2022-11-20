package CardAugments.commands;

import basemod.DevConsole;
import basemod.devcommands.ConsoleCommand;

public class ChimeraHand extends ConsoleCommand {
    public ChimeraHand() {
        followup.put("mod", ChimeraHandMod.class);
        followup.put("add", ChimeraHandAdd.class);
        followup.put("force", ChimeraHandForce.class);
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