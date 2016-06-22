package com.intellectualsites.rectangular.manager;

import com.intellectualsites.rectangular.player.PlayerMeta;
import com.intellectualsites.rectangular.player.RectangularPlayer;

import java.util.UUID;

public interface PlayerManager {

    RectangularPlayer getPlayer(String username);
    RectangularPlayer getPlayer(UUID uuid);
    PlayerMeta unloadMeta(UUID uuid);
    void loadMeta(UUID uuid);

}
