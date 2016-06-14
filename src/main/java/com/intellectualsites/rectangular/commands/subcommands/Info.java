package com.intellectualsites.rectangular.commands.subcommands;

import com.intellectualsites.rectangular.commands.SubCommand;
import com.intellectualsites.rectangular.core.Region;
import com.intellectualsites.rectangular.player.RectangularPlayer;

import java.util.Arrays;
import java.util.List;

public class Info extends SubCommand {

    public Info() {
        super("info", Arrays.asList("i", "about"));
    }

    @Override
    public void execute(RectangularPlayer player, List<String> arguments) {
        if (player.isInRegion()) {
            player.sendMessage("@error.not_in_region");
        } else {
            Region region = player.getRegion();
            player.sendMessage("@error.not_implemented");
        }
    }

}
