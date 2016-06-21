package com.intellectualsites.rectangular.command;

import com.intellectualsites.commands.argument.ArgumentType;
import com.intellectualsites.rectangular.Rectangular;
import com.intellectualsites.rectangular.player.RectangularPlayer;

import java.util.UUID;

public class PlayerArg extends ArgumentType<RectangularPlayer> {

    public PlayerArg() {
        super("player", null);
    }

    @Override
    public RectangularPlayer parse(String in) {
        if (in.length() > 16) {
            return Rectangular.get().getServiceManager().getPlayerManager().getPlayer(UUID.fromString(in));
        }
        return Rectangular.get().getServiceManager().getPlayerManager().getPlayer(in);
    }
}
