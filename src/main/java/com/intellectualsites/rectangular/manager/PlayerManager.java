package com.intellectualsites.rectangular.manager;

import com.intellectualsites.rectangular.player.RectangularPlayer;

import java.util.UUID;

public interface PlayerManager {

    RectangularPlayer getPlayer(String username);
    RectangularPlayer getPlayer(UUID uuid);

}
