package com.intellectualsites.rectangular.bukkit.nms.v1_10_R0_1;

import net.minecraft.server.v1_10_R1.NBTTagCompound;
import org.bukkit.craftbukkit.v1_10_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

class ItemNBTManager implements com.intellectualsites.rectangular.bukkit.ItemNBTManager {

    // Lazy utility method
    private net.minecraft.server.v1_10_R1.ItemStack getItemStack(ItemStack itemStack) {
        return CraftItemStack.asNMSCopy(itemStack);
    }

    // Lazy utility method
    private NBTTagCompound getNBT(ItemStack itemStack) {
        NBTTagCompound compound = getItemStack(itemStack).getTag();
        if (compound == null) {
            // It is @Nullable :(
            return new NBTTagCompound();
        }
        return compound;
    }

    private void setNBT(ItemStack itemStack, NBTTagCompound nbt) {
        getItemStack(itemStack).setTag(nbt);
    }

    @Override
    public void setString(ItemStack itemStack, String key, String value) {
        NBTTagCompound compound = getNBT(itemStack);
        compound.setString(key, value);
        setNBT(itemStack, compound);
    }

    @Override
    public String getString(ItemStack itemStack, String key) {
        return getNBT(itemStack).getString(key);
    }

    @Override
    public void setInt(ItemStack itemStack, String key, int value) {
        NBTTagCompound compound = getNBT(itemStack);
        compound.setInt(key, value);
        setNBT(itemStack, compound);
    }

    @Override
    public int getInt(ItemStack itemStack, String key) {
        return getNBT(itemStack).getInt(key);
    }

    @Override
    public void setDouble(ItemStack itemStack, String key, double value) {
        NBTTagCompound compound = getNBT(itemStack);
        compound.setDouble(key, value);
        setNBT(itemStack, compound);
    }

    @Override
    public double getDouble(ItemStack itemStack, String key) {
        return getNBT(itemStack).getDouble(key);
    }

    @Override
    public void setBoolean(ItemStack itemStack, String key, boolean value) {
        NBTTagCompound compound = getNBT(itemStack);
        compound.setBoolean(key, value);
        setNBT(itemStack, compound);
    }

    @Override
    public boolean getBoolean(ItemStack itemStack, String key) {
        return getNBT(itemStack).getBoolean(key);
    }

    @Override
    public boolean hasKey(ItemStack itemStack, String key) {
        return getNBT(itemStack).hasKey(key);
    }
}
