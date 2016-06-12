package com.intellectualsites.rectangular.bukkit;

import com.intellectualsites.rectangular.vector.Vector2;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class BukkitUtil {

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

    public static Location vectorToLocation(World world, Vector2 vector2, int y) {
        return new Location(world, vector2.getX(), y, vector2.getY());
    }

    public static Location vectorToLocation(World world, Vector2 vector2) {
        return new Location(world, vector2.getX(), world.getHighestBlockYAt(vector2.getX(), vector2.getY()), vector2.getY());
    }


}
