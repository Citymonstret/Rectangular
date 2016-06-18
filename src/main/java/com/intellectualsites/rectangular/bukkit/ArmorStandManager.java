package com.intellectualsites.rectangular.bukkit;

import org.bukkit.DyeColor;

public interface ArmorStandManager {

    int spawn(BukkitPlayer player, double x, double y, double z, DyeColor color);
    void despawn(BukkitPlayer player, int ... ids);

}
