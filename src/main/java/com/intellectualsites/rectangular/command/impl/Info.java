package com.intellectualsites.rectangular.command.impl;

import com.intellectualsites.commands.Command;
import com.intellectualsites.commands.CommandDeclaration;
import com.intellectualsites.commands.CommandInstance;
import com.intellectualsites.rectangular.player.RectangularPlayer;

@CommandDeclaration(
        command = "info",
        aliases = { "i", "about" }
)
public class Info extends Command {

    @Override
    public boolean onCommand(CommandInstance instance) {
        RectangularPlayer player = (RectangularPlayer) instance.getCaller();
        if (!player.isInRegion()) {
            player.sendMessage("@error.not_in_region");
        } else {
            player.sendMessage("You're in region: " + player.getRegion().getId());
        }
        return true;
    }

}
