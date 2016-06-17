package com.intellectualsites.rectangular.commands.subcommands;

import com.intellectualsites.rectangular.commands.RectangularCommand;
import com.intellectualsites.rectangular.core.Region;
import com.intellectualsites.rectangular.player.RectangularPlayer;

import java.util.List;

public class Info implements RectangularCommand {

    @Override
    public void execute(RectangularPlayer player, List<String> arguments) {
        if (!player.isInRegion()) {
            player.sendMessage("@error.not_in_region");
        } else {
            Region region = player.getRegion();
            player.sendMessage("@error.not_implemented");
        }
    }

}
