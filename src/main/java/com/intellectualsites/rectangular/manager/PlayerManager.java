package com.intellectualsites.rectangular.manager;

import com.intellectualsites.rectangular.player.PlayerMeta;
import com.intellectualsites.rectangular.player.RectangularPlayer;

import java.util.UUID;

public interface PlayerManager {

    RectangularPlayer getPlayer(String username);
    RectangularPlayer getPlayer(UUID uuid);

    /**
     * This should not be used unless you know what
     * you are doing
     */
    PlayerMeta unloadMeta(UUID uuid);

    /**
     * This should not be used unless you know what
     * you are doing
     */
    void loadMeta(UUID uuid);

}
