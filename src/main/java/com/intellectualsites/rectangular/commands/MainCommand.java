package com.intellectualsites.rectangular.commands;

import com.intellectualsites.commands.CommandInstance;
import com.intellectualsites.commands.bukkit.argument.Player;
import com.intellectualsites.commands.bukkit.plugin.PluginCommand;

public class MainCommand extends PluginCommand {

    @Override
    public boolean onCommand(CommandInstance instance) {
        Player player = (Player) instance.getCaller().getSuperCaller();
        return true;
    }
}
