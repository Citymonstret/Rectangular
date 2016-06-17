package com.intellectualsites.rectangular.bukkit;

import com.intellectualsites.rectangular.Rectangular;
import com.intellectualsites.rectangular.core.Region;
import com.intellectualsites.rectangular.core.WorldContainer;
import com.intellectualsites.rectangular.item.Item;
import com.intellectualsites.rectangular.player.PlayerEventObserver;
import com.intellectualsites.rectangular.player.RectangularPlayer;
import lombok.Getter;
import net.minecraft.server.v1_10_R1.*;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.craftbukkit.v1_10_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_10_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_10_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.material.Wool;

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
        return topLevelRegion; // TODO: FIX!
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
        return "w:" + player.getWorld().getName();
    }

    @Override
    public WorldContainer getWorldObject() {
        return Rectangular.get().getWorldManager().getWorldContainer(getWorld());
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
        PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(armorStandCache.get(key));
        armorStandCache.remove(key);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }

    @Override
    public void deleteIndicators() {
        int[] ids = new int[armorStandCache.size()];
        final int[] index = {0};
        armorStandCache.values().forEach(i -> ids[index[0]++] = i);
        PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(ids);
        armorStandCache.clear();
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }

    @Override
    public void giveItem(Item item) {
        player.getInventory().addItem(BukkitUtil.itemToItemStack(item));
    }

    @Override
    public void showIndicator(double x, double y, double z, String colour) {
        WorldServer server = ((CraftWorld) player.getWorld()).getHandle();
        EntityArmorStand armorStand = new EntityArmorStand(server);
        armorStand.setLocation(x, y, z, 0f, 0f);
        armorStand.setInvisible(true);
        armorStand.setSmall(true);
        armorStand.setNoGravity(true);
        armorStand.setSilent(true);
        armorStand.setInvulnerable(true);
        Packet packet = new PacketPlayOutSpawnEntityLiving(armorStand);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
        Wool wool = new Wool(DyeColor.valueOf(colour.toUpperCase()));
        packet = new PacketPlayOutEntityEquipment(armorStand.getId(), EnumItemSlot.HEAD, CraftItemStack.asNMSCopy(wool.toItemStack()));
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
        armorStandCache.put(x + ";" + y + ";" + z, armorStand.getId());
    }
}
