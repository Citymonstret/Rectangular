package com.intellectualsites.rectangular.player;

import java.util.UUID;

public interface RectangularPlayer {

    UUID getUniqueId();

    int getId();

    boolean isOp();

    boolean hasPermission(String permissionNode);

}
