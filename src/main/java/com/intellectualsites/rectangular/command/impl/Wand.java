package com.intellectualsites.rectangular.command.impl;

import com.intellectualsites.commands.Command;
import com.intellectualsites.commands.CommandDeclaration;
import com.intellectualsites.commands.CommandInstance;
import com.intellectualsites.rectangular.Rectangular;
import com.intellectualsites.rectangular.config.Message;
import com.intellectualsites.rectangular.player.RectangularPlayer;

@CommandDeclaration(
        command = "wand",
        aliases = {"tool", "w"}
)
public class Wand extends Command {

    @Override
    public boolean onCommand(CommandInstance instance) {
        if (!Rectangular.get().getServiceManager().getSelectionManager().equipPlayer((RectangularPlayer) instance.getCaller())) {
            Message.ERROR_HAS_TOOL.send(instance.getCaller());
        }
        return true;
    }

}
