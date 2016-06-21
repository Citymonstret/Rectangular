package com.intellectualsites.rectangular.bukkit;

import com.intellectualsites.commands.Command;
import com.intellectualsites.commands.CommandManager;
import com.intellectualsites.commands.argument.Argument;
import com.intellectualsites.rectangular.Rectangular;
import com.intellectualsites.rectangular.core.Region;
import com.intellectualsites.rectangular.core.WorldContainer;
import com.intellectualsites.rectangular.item.Item;
import com.intellectualsites.rectangular.player.PlayerEventObserver;
import com.intellectualsites.rectangular.player.RectangularPlayer;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BukkitPlayer implements RectangularPlayer {

    private static int idPool = Integer.MIN_VALUE;

    @Getter
    private final int id;

    @Getter
    private final Player player;

    @Getter
    private final PlayerEventObserver eventObserver;

    private Region topLevelRegion;

    private boolean regionFetched = false;

    public BukkitPlayer(Player player) {
        this.id = idPool++;
        this.player = player;
        this.eventObserver = new PlayerEventObserver(this);
    }

    @Override
    public UUID getUniqueId() {
        return player.getUniqueId();
    }

    @Override
    public boolean isOp() {
        return player.isOp();
    }

    @Override
    public boolean hasPermission(String permissionNode) {
        return player.hasPermission(permissionNode);
    }

    @Override
    public boolean isInRegion() {
        return getRegion() != null;
    }

    @Override
    public Region getRegion() {
        if (!regionFetched) {
            regionFetched = true;
            resetRegionCache();
        }
        return topLevelRegion;
    }

    @Override
    public void resetRegionCache() {
        Region old = topLevelRegion;
        topLevelRegion = Rectangular.get().getRegionManager().
                getHighestLevelRegion(getWorld(),
                        BukkitUtil.locationToVector(player.getLocation()));
        if (topLevelRegion != null && topLevelRegion != old) {
            getEventObserver().onPlayerEnterRegion();
        }
    }

    @Override
    public String getWorld() {
        return player.getWorld().getName();
    }

    @Override
    public WorldContainer getWorldObject() {
        return Rectangular.get().getWorldManager().getContainer(getWorld());
    }

    @Override
    public void sendMessage(String msg) {
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
    }

    private Map<String, Integer> armorStandCache = new HashMap<>();

    @Override
    public void deleteIndicator(double x, double y, double z) {
        String key = x + ";" + y + ";" + z;
        if (!armorStandCache.containsKey(key)) {
            return;
        }
        RectangularPlugin.getNmsImplementation().getArmorStandManager().despawn(this, armorStandCache.get(key));
        armorStandCache.remove(key);
    }

    @Override
    public void deleteIndicators() {
        int[] ids = new int[armorStandCache.size()];
        final int[] index = {0};
        armorStandCache.values().forEach(i -> ids[index[0]++] = i);
        armorStandCache.clear();
        RectangularPlugin.getNmsImplementation().getArmorStandManager().despawn(this, ids);
    }

    @Override
    public void giveItem(Item item) {
        org.bukkit.inventory.ItemStack itemStack = BukkitUtil.itemToItemStack(item);
        player.getInventory().addItem(itemStack);
        player.updateInventory();
    }

    @Override
    public void showIndicator(double x, double y, double z, String colour) {
        if (armorStandCache.containsKey( x + ";" + y + ";" + z)) {
            return; // Otherwise it will create buggy duplicates :/
        }
        armorStandCache.put(x + ";" + y + ";" + z, RectangularPlugin.getNmsImplementation().getArmorStandManager().spawn(this, x, y, z, DyeColor.valueOf(colour.toUpperCase())));
    }

    @Override
    public boolean hasAttachment(String a) {
        return player.hasPermission(a);
    }

    @Override
    public void sendRequiredArgumentsList(CommandManager manager, Command cmd, Collection<Argument> required, String usage) {

    }
}
