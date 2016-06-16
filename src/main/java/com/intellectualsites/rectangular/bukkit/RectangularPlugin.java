package com.intellectualsites.rectangular.bukkit;

import com.intellectualsites.rectangular.Rectangular;
import com.intellectualsites.rectangular.commands.MainCommand;
import com.intellectualsites.rectangular.commands.SubCommand;
import com.intellectualsites.rectangular.commands.subcommands.Info;
import com.intellectualsites.rectangular.core.Rectangle;
import com.intellectualsites.rectangular.core.Region;
import com.intellectualsites.rectangular.manager.ServiceManager;
import com.intellectualsites.rectangular.manager.WorldManager;
import com.intellectualsites.rectangular.vector.Vector2;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.*;

public class RectangularPlugin extends JavaPlugin implements ServiceManager {

    private void setupCommands() {
        List<SubCommand> subCommands = new ArrayList<>();
        subCommands.add(new Info());
        getCommand("rectangular").setExecutor(new MainCommand(subCommands));
    }

    @Override
    public void onEnable() {
        this.setupCommands();

        getServer().getScheduler().runTaskTimer(this, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                BukkitPlayer bukkitPlayer = BukkitUtil.getPlayer(player);
                bukkitPlayer.deleteIndicators();

                if (bukkitPlayer.isInRegion()) {
                    Region r = bukkitPlayer.getRegion();
                    for (Rectangle re : r.getRectangles()) {
                        for (Vector2 vector2 : re.getOutline()) {
                            Location location = BukkitUtil.vectorToLocation(player.getWorld(), vector2, player.getLocation().getY() + 0.5d);
                            location.add(0, 1.3d, 0);
                            bukkitPlayer.showIndicator(location.getX(), location.getY(), location.getZ(),
                                    DyeColor.values()[(int)(Math.random() * DyeColor.values().length)].name());
                        }
                    }
                }
            }
        }, 0L, 5L);

        try {
            Rectangular.setup(this);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public WorldManager getWorldManager() {
        Set<String> worlds = new HashSet<>();
        Bukkit.getWorlds().forEach(world -> worlds.add(world.getName()));
        return new WorldManager(worlds);
    }

    @Override
    public void shutdown(String reason) {
        getLogger().severe("Shutting down: " + reason);
        getServer().getPluginManager().disablePlugin(this);
    }

    @Override
    public void runAsync(Runnable r) {
        getServer().getScheduler().runTaskAsynchronously(this, r);
    }

    @Override
    public File getFolder() {
        return getDataFolder();
    }
}
