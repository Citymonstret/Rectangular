package com.intellectualsites.rectangular.parser.impl;

import com.intellectualsites.rectangular.Rectangular;
import com.intellectualsites.rectangular.parser.Parser;
import com.intellectualsites.rectangular.player.RectangularPlayer;

import java.util.UUID;

public class PlayerParser extends Parser<RectangularPlayer> {

    public PlayerParser() {
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
