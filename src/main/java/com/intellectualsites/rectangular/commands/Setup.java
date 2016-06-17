package com.intellectualsites.rectangular.commands;

import com.intellectualsites.rectangular.Rectangular;
import com.intellectualsites.rectangular.player.RectangularPlayer;
import com.intellectualsites.rectangular.selection.SelectionManager;

import java.util.Arrays;
import java.util.List;

public class Setup extends SubCommand {

    public Setup() {
        super("setup", Arrays.asList("create", "s"));
    }

    @Override
    public void execute(RectangularPlayer player, List<String> arguments) {
        if (player.isInRegion()) {
            player.sendMessage("@error.use_expand");
        } else {
            if (arguments.size() > 0) {
                String first = arguments.get(0);
                if (first.equalsIgnoreCase("cancel")) {
                    player.sendMessage("@error.not_implemented");
                    return;
                } else if (first.equalsIgnoreCase("finish")) {
                    player.sendMessage("@error.not_implemented");
                    return;
                }
            }
            SelectionManager selectionManager = Rectangular.get().getServiceManager().getSelectionManager();
            selectionManager.equipPlayer(player);
            player.sendMessage("@setup.equipped");
        }
    }
}
