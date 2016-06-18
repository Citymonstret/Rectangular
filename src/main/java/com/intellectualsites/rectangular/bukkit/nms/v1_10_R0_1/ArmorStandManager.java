package com.intellectualsites.rectangular.bukkit.nms.v1_10_R0_1;

import com.intellectualsites.rectangular.bukkit.BukkitPlayer;
import net.minecraft.server.v1_10_R1.*;
import org.bukkit.DyeColor;
import org.bukkit.craftbukkit.v1_10_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_10_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_10_R1.inventory.CraftItemStack;
import org.bukkit.material.Wool;

public class ArmorStandManager implements com.intellectualsites.rectangular.bukkit.ArmorStandManager {

    @Override
    public int spawn(BukkitPlayer player, double x, double y, double z, DyeColor color) {
        WorldServer server = ((CraftWorld) player.getPlayer().getWorld()).getHandle();
        EntityArmorStand armorStand = new EntityArmorStand(server);
        armorStand.setLocation(x, y, z, 0f, 0f);
        armorStand.setInvisible(true);
        armorStand.setSmall(true);
        armorStand.setNoGravity(true);
        armorStand.setSilent(true);
        armorStand.setInvulnerable(true);
        Packet packet = new PacketPlayOutSpawnEntityLiving(armorStand);
        ((CraftPlayer) player.getPlayer()).getHandle().playerConnection.sendPacket(packet);
        Wool wool = new Wool(color);
        packet = new PacketPlayOutEntityEquipment(armorStand.getId(), EnumItemSlot.HEAD, CraftItemStack.asNMSCopy(wool.toItemStack()));
        ((CraftPlayer) player.getPlayer()).getHandle().playerConnection.sendPacket(packet);
        return armorStand.getId();
    }

    @Override
    public void despawn(BukkitPlayer player, int... ids) {
        Packet packet = new PacketPlayOutEntityDestroy(ids);
        ((CraftPlayer) player.getPlayer()).getHandle().playerConnection.sendPacket(packet);
    }

}

