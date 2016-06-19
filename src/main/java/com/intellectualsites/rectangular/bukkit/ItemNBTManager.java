package com.intellectualsites.rectangular.bukkit;

import org.bukkit.inventory.ItemStack;

public interface ItemNBTManager {

    void setString(ItemStack itemStack, String key, String value);

    String getString(ItemStack itemStack, String key);

    void setInt(ItemStack itemStack, String key, int value);

    int getInt(ItemStack itemStack, String key);

    void setDouble(ItemStack itemStack, String key, double value);

    double getDouble(ItemStack itemStack, String key);

    void setBoolean(ItemStack itemStack, String key, boolean value);

    boolean getBoolean(ItemStack itemStack, String key);

    boolean hasKey(ItemStack itemStack, String key);

}
