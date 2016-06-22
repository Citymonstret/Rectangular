package com.intellectualsites.rectangular.player;

import com.intellectualsites.commands.callers.CommandCaller;
import com.intellectualsites.rectangular.api.objects.Region;
import com.intellectualsites.rectangular.core.WorldContainer;
import com.intellectualsites.rectangular.item.Item;

import java.util.UUID;

public interface RectangularPlayer extends CommandCaller<RectangularPlayer> {

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

    void giveItem(Item item);

    PlayerMeta getMeta();

    @Override
    default RectangularPlayer getSuperCaller() {
        return this;
    }

    @Override
    default void message(String message) {
        this.sendMessage(message);
    }
}
