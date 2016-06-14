package com.intellectualsites.rectangular.commands;

import com.intellectualsites.rectangular.player.RectangularPlayer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public abstract class SubCommand {

    @Getter
    private final String command;

    @Getter
    private final List<String> aliases;

    public abstract void execute(RectangularPlayer player, List<String> arguments);

    public boolean hasPermission(RectangularPlayer player, List<String> arguments) {
        return player.isOp() ||
                player.hasPermission("rectangular.admin") ||
                player.hasPermission("rectangular." + command);
    }

}
