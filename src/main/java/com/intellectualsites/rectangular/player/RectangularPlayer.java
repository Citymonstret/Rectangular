package com.intellectualsites.rectangular.player;

import com.intellectualsites.rectangular.core.Region;
import com.intellectualsites.rectangular.core.WorldContainer;

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

    void showIndicator(double x, double y, double z, String colour);

    void deleteIndicator(double x, double y, double z);

    String getWorld();

    WorldContainer getWorldObject();

    void deleteIndicators();

    PlayerEventObserver getEventObserver();
}
