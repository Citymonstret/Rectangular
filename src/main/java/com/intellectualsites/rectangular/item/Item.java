package com.intellectualsites.rectangular.item;

import com.google.common.collect.ImmutableList;
import lombok.Getter;

import java.util.*;

public class Item {

    @Getter
    private Material material;

    @Getter
    private int durability;

    @Getter
    private int stackSize;

    @Getter
    private String displayName;

    @Getter
    private ImmutableList<String> lore = new ImmutableList.Builder<String>().build();

    private final Map<String, Object> itemMeta = new HashMap<>();
    private final List<String> volatileItems = new ArrayList<>();

    public boolean hasMetaItem(String key) {
        return itemMeta.containsKey(key);
    }

    public Object getMetaItem(String key) {
        return itemMeta.get(key);
    }

    public void removeMetaItem(String key) {
        if (volatileItems.contains(key)) {
            volatileItems.remove(key);
        }
        itemMeta.remove(key);
    }

    public void setMetaItem(String key, Object value, boolean vol) {
        this.itemMeta.put(key, value);
        if (vol) {
            volatileItems.add(key);
        }
    }

    public Item() {
        this(Material.AIR);
    }

    public Item(Material material) {
        this(material, material.getDurability(), material.getStackSize());
    }

    public Item(Material material, String name) {
        this(material, material.getDurability(), material.getStackSize(), name);
    }

    public Item(Material material, int durability) {
        this(material, durability, material.getStackSize());
    }

    public Item(Material material, int durability, int stackSize) {
        this(material, durability, stackSize, "");
    }

    public Item(Material material, int durability, int stackSize, String name) {
        this.material = material;
        this.durability = durability;
        this.stackSize = stackSize;
        this.displayName = name;
    }

    private void clearVolatile() {
        volatileItems.forEach(itemMeta::remove);
        volatileItems.clear();
    }

    public void setMaterial(Material material) {
        this.material = material;
        clearVolatile();
    }

    public void setDurability(int durability) {
        this.durability = durability;
        clearVolatile();
    }

    public void setStackSize(int stackSize) {
        this.stackSize = stackSize;
        clearVolatile();
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
        clearVolatile();
    }

    public void setLore(Collection<String> lore) {
        this.lore = ImmutableList.copyOf(lore);
        clearVolatile();
    }
}
