package com.intellectualsites.rectangular.bukkit;

import com.google.common.eventbus.Subscribe;
import com.intellectualsites.rectangular.Rectangular;
import com.intellectualsites.rectangular.bukkit.listener.PlayerListener;
import com.intellectualsites.rectangular.bukkit.nms.NMSImplementation;
import com.intellectualsites.rectangular.commands.MainCommand;
import com.intellectualsites.rectangular.commands.RectangularCommand;
import com.intellectualsites.rectangular.commands.SubCommand;
import com.intellectualsites.rectangular.commands.subcommands.Info;
import com.intellectualsites.rectangular.core.Rectangle;
import com.intellectualsites.rectangular.core.Region;
import com.intellectualsites.rectangular.event.RectangularListener;
import com.intellectualsites.rectangular.event.impl.PlayerEnteredRegionEvent;
import com.intellectualsites.rectangular.event.impl.PlayerLeftRegionEvent;
import com.intellectualsites.rectangular.event.impl.RegionManagerDoneEvent;
import com.intellectualsites.rectangular.logging.RectangularLogger;
import com.intellectualsites.rectangular.manager.ServiceManager;
import com.intellectualsites.rectangular.manager.WorldManager;
import com.intellectualsites.rectangular.selection.SelectionManager;
import com.intellectualsites.rectangular.vector.Vector2;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class RectangularPlugin extends JavaPlugin implements ServiceManager, RectangularLogger, RectangularListener {

    private SelectionManager selectionManager;

    @Getter
    private static NMSImplementation nmsImplementation;

    @Override
    public void onEnable() {
        String version = Bukkit.getServer().getClass().getPackage().getName().replace(".",  ",").split(",")[3];
        if (version.equals("v1_10_R1")) {
            nmsImplementation = new com.intellectualsites.rectangular.bukkit.nms.v1_10_R0_1.NMSImplementation();
            getLogger().info("Running NMSImplementation: " + nmsImplementation.getImplementationName() + " | " + version);
        } else {
            shutdown("You are not running a supported server version");
        }

        this.selectionManager = new BukkitSelectionManager();

        Map<SubCommand, RectangularCommand> subCommands = new HashMap<>();
        subCommands.put(SubCommand.INFO, new Info());
        subCommands.put(SubCommand.SETUP, (BukkitSelectionManager) selectionManager);
        getCommand("rectangular").setExecutor(new MainCommand(subCommands));

        this.getServer().getPluginManager().registerEvents(new PlayerListener(), this);

        try {
            Rectangular.setup(this);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        Rectangular.get().getEventManager().register(this);
    }

    @Subscribe
    public void onPlayerLeftRegion(PlayerLeftRegionEvent event) {
        event.getPlayer().sendMessage("&cYou left region: " + event.getOldRegion().getId());
    }

    @Subscribe
    public void onPlayerEnterRegion(PlayerEnteredRegionEvent event) {
        event.getPlayer().sendMessage("&cYou entered region: " + event.getRegion().getId());
    }

    @Subscribe
    public void onRegionManagerLoad(RegionManagerDoneEvent event) {
        // start #temp1 ::= Temporary test code
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
        // emd #temp1
    }

    @Override
    public WorldManager getWorldManager() {
        Set<String> worlds = new HashSet<>();
        Bukkit.getWorlds().forEach(world -> worlds.add(world.getName()));
        return new WorldManager(worlds);
    }

    @Override
    public RectangularLogger logger() {
        return this;
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
    public void runSync(Runnable r) {
        getServer().getScheduler().runTask(this, r);
    }

    @Override
    public SelectionManager getSelectionManager() {
        return selectionManager;
    }

    @Override
    public File getFolder() {
        return getDataFolder();
    }

    @Override
    public RectangularLogger info(String str) {
        getLogger().info(str);
        return this;
    }
}
