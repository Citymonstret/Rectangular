package com.intellectualsites.rectangular.commands;

import com.intellectualsites.rectangular.bukkit.BukkitUtil;
import com.intellectualsites.rectangular.player.RectangularPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class MainCommand implements CommandExecutor {

    private final Map<String, SubCommand> commandMap = new HashMap<>();
    private final Map<String, String> aliasMap = new HashMap<>();

    public MainCommand(Collection<SubCommand> subCommands) {
        for (SubCommand subCommand : subCommands) {
            commandMap.put(subCommand.getCommand(), subCommand);
            for (String alias : subCommand.getAliases()) {
                aliasMap.put(alias, subCommand.getCommand());
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            return true; // TODO: Console commands
        }

        RectangularPlayer rectangularPlayer = BukkitUtil.getPlayer((Player) commandSender);

        String commandLabel;
        List<String> arguments = new ArrayList<>();

        if (strings.length < 1) {
            commandLabel = "help";
        } else {
            commandLabel = strings[0].toLowerCase();
            if (strings.length > 1) {
                arguments.addAll(Arrays.asList(strings).subList(1, strings.length));
            }
        }

        SubCommand subCommand = null;
        if (!commandMap.containsKey(commandLabel)) {
            if (aliasMap.containsKey(commandLabel)) {
                subCommand = commandMap.get(aliasMap.get(commandLabel));
            }
        } else {
            subCommand = commandMap.get(commandLabel);
        }

        if (subCommand == null) {
            rectangularPlayer.sendMessage("@error.no_such_command");
        } else {
            if (!subCommand.hasPermission(rectangularPlayer, arguments)) {
                rectangularPlayer.sendMessage("@error.no_permission");
            } else {
                subCommand.execute(rectangularPlayer, arguments);
            }
        }
        return true;
    }

}
