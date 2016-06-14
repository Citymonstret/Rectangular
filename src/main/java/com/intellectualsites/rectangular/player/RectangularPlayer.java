package com.intellectualsites.rectangular.player;

import com.intellectualsites.rectangular.core.Region;

import java.util.UUID;

public interface RectangularPlayer {

    UUID getUniqueId();

    int getId();

    boolean isOp();

    boolean hasPermission(String permissionNode);

    boolean isInRegion();

    Region getRegion();

    void resetRegionCache();

    void sendMessage(String msg);
}
