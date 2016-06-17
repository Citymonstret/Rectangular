package com.intellectualsites.rectangular.bukkit;

import com.intellectualsites.rectangular.item.Item;
import com.intellectualsites.rectangular.vector.Vector2;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.UUID;

public class BukkitUtil {

    private static final String ITEM_META_BUKKIT_ITEMSTACK = "bukkitItemStack";

    private static final HashMap<String, BukkitPlayer> playerRegistry = new HashMap<>();

    public static BukkitPlayer getPlayer(Player player) {
        if (!playerRegistry.containsKey(player.getUniqueId().toString())) {
            playerRegistry.put(player.getUniqueId().toString(), new BukkitPlayer(player));
        }
        return playerRegistry.get(player.getUniqueId().toString());
    }

    public static void removePlayer(UUID uuid) {
        playerRegistry.remove(uuid.toString());
    }

    public static Vector2 locationToVector(Location location) {
        return new Vector2(location.getBlockX(), location.getBlockZ());
    }

    public static Location vectorToLocation(World world, Vector2 vector2, double y) {
        return new Location(world, vector2.getX(), y, vector2.getY());
    }

    public static Location vectorToLocation(World world, Vector2 vector2) {
        return new Location(world, vector2.getX(), world.getHighestBlockYAt(vector2.getX(), vector2.getY()), vector2.getY());
    }

    public static ItemStack itemToItemStack(Item item) {
        if (item.hasMetaItem(ITEM_META_BUKKIT_ITEMSTACK)) {
            return (ItemStack) item.getMetaItem(ITEM_META_BUKKIT_ITEMSTACK);
        }
        ItemStack itemStack = new ItemStack(Material.valueOf(item.getMaterial().name()), item.getStackSize(), (short) item.getDurability());
        if (!item.getDisplayName().isEmpty()) {
           itemStack.getItemMeta().setDisplayName(item.getDisplayName());
        }
        if (!item.getLore().isEmpty()) {
            itemStack.getItemMeta().setLore(item.getLore());
        }
        return itemStack;
    }

    public static Item itemStackToItem(ItemStack itemStack) {
        Item item = new Item(com.intellectualsites.rectangular.item.Material.valueOf(itemStack.getType().name()), itemStack.getDurability(), itemStack.getAmount());
        if (itemStack.hasItemMeta()) {
            if (itemStack.getItemMeta().hasDisplayName()) {
                item.setDisplayName(itemStack.getItemMeta().getDisplayName());
            }
            if (itemStack.getItemMeta().hasLore()) {
                item.getLore().addAll(itemStack.getItemMeta().getLore());
            }
        }
        item.setMetaItem(ITEM_META_BUKKIT_ITEMSTACK, itemStack, true); // Cache the Bukkit item
        return item;
    }
}
