package com.intellectualsites.rectangular.bukkit.listener;

import com.intellectualsites.rectangular.Rectangular;
import com.intellectualsites.rectangular.api.objects.Region;
import com.intellectualsites.rectangular.bukkit.BukkitPlayer;
import com.intellectualsites.rectangular.bukkit.BukkitUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {

    @EventHandler
    public void onPlayerLogout(PlayerQuitEvent event) {
        // Do this lastly
        BukkitUtil.removePlayer(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPreLogin(AsyncPlayerPreLoginEvent event) {
        Rectangular.get().getServiceManager().getPlayerManager().loadMeta(event.getUniqueId());
    }

    @EventHandler
    public void onPlayerMoveEvent(PlayerMoveEvent event) {
        BukkitPlayer player = BukkitUtil.getPlayer(event.getPlayer());
        Region old = player.getRegion();

        int oldX = event.getFrom().getBlockX();
        int oldZ = event.getFrom().getBlockZ();
        int newX = event.getTo().getBlockX();
        int newZ = event.getTo().getBlockZ();

        if  (oldX == newX && oldZ == newZ) {
            return;
        }

        if (old != null) { // Were in a region, check if still is
            if (!player.getRegion().isInRegion(BukkitUtil.locationToVector(event.getPlayer().getLocation()))) { // Player is no longer in that region
                player.resetRegionCache();
                player.getEventObserver().onPlayerLeaveRegion(old);
            }
        } else {
            player.resetRegionCache();
        }
    }
}
