package com.intellectualsites.rectangular.command.impl;

import com.intellectualsites.commands.Command;
import com.intellectualsites.commands.CommandDeclaration;
import com.intellectualsites.commands.CommandInstance;
import com.intellectualsites.rectangular.parser.impl.StringParser;
import com.intellectualsites.rectangular.player.RectangularPlayer;

@CommandDeclaration(
        command = "setmeta"
)
public class SetMeta extends Command {

    public SetMeta() {
        withArgument("key", new StringParser(), "The meta key");
        withArgument("value", new StringParser(), "The meta value");
    }

    @Override
    public boolean onCommand(CommandInstance instance) {
        RectangularPlayer player = (RectangularPlayer) instance.getCaller();
        player.getMeta().setMeta(instance.getString("key"), instance.getString("value"));
        player.sendMessage(instance.getString("key") + ": " + player.getMeta().getString(instance.getString("key")));
        return true;
    }

}
