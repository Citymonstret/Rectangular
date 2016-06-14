package com.intellectualsites.rectangular.bukkit;

import com.intellectualsites.rectangular.Rectangular;
import com.intellectualsites.rectangular.core.Region;
import com.intellectualsites.rectangular.player.RectangularPlayer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public class BukkitPlayer implements RectangularPlayer {

    private static int idPool = Integer.MIN_VALUE;

    @Getter
    private final int id;

    @Getter
    private final Player player;

    private Region topLevelRegion;

    public BukkitPlayer(Player player) {
        this.id = idPool++;
        this.player = player;
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
        // TODO: Add caching! (Update on events)
        if (topLevelRegion == null) {
            resetRegionCache(); // Will force fetch it
        }
        return topLevelRegion;
    }

    @Override
    public void resetRegionCache() {
        topLevelRegion = Rectangular.get().getRegionManager().
                getHighestLevelRegion(player.getWorld().getName(),
                        BukkitUtil.locationToVector(player.getLocation()));
    }

    @Override
    public void sendMessage(String msg) {
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
    }

}
