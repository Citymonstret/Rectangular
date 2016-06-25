package com.intellectualsites.rectangular.bukkit;

import com.google.common.collect.ImmutableList;
import com.google.common.eventbus.Subscribe;
import com.intellectualsites.commands.Command;
import com.intellectualsites.commands.CommandHandlingOutput;
import com.intellectualsites.rectangular.Rectangular;
import com.intellectualsites.rectangular.api.objects.Region;
import com.intellectualsites.rectangular.bukkit.listener.PlayerListener;
import com.intellectualsites.rectangular.bukkit.nms.NMSImplementation;
import com.intellectualsites.rectangular.core.Rectangle;
import com.intellectualsites.rectangular.event.RectangularListener;
import com.intellectualsites.rectangular.event.impl.PlayerEnteredRegionEvent;
import com.intellectualsites.rectangular.event.impl.PlayerLeftRegionEvent;
import com.intellectualsites.rectangular.event.impl.RegionManagerDoneEvent;
import com.intellectualsites.rectangular.logging.RectangularLogger;
import com.intellectualsites.rectangular.manager.PlayerManager;
import com.intellectualsites.rectangular.manager.ServiceManager;
import com.intellectualsites.rectangular.manager.WorldManager;
import com.intellectualsites.rectangular.player.PlayerMeta;
import com.intellectualsites.rectangular.player.RectangularPlayer;
import com.intellectualsites.rectangular.selection.SelectionManager;
import com.intellectualsites.rectangular.vector.Vector2;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class RectangularPlugin extends JavaPlugin implements ServiceManager, RectangularLogger, RectangularListener, PlayerManager {

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
        this.getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        try {
            Rectangular.setup(this);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        final Command commandManager = Rectangular.get().getCommandManager();
        getCommand("rectangular").setExecutor((commandSender, command, s, strings) -> commandSender instanceof ConsoleCommandSender
                || commandManager.handle(BukkitUtil.getPlayer((Player) commandSender), strings).getCommandResult() == CommandHandlingOutput.SUCCESS);
        Rectangular.get().getEventManager().register(this);
        getServer().getPluginManager().registerEvents((BukkitSelectionManager) this.selectionManager, this);
    }

    @Subscribe
    public void onPlayerLeftRegion(final PlayerLeftRegionEvent event) {
        event.getPlayer().sendMessage("&cYou left region: " + event.getOldRegion().getId());
    }

    @Subscribe
    public void onPlayerEnterRegion(final PlayerEnteredRegionEvent event) {
        event.getPlayer().sendMessage("&cYou entered region: " + event.getRegion().getId());
    }

    @Subscribe
    public void onRegionManagerLoad(final RegionManagerDoneEvent event) {
        // start #temp1 ::= Temporary test code
        this.getServer().getScheduler().runTaskTimer(this, () -> {
            for (final Player player : Bukkit.getOnlinePlayers()) {
                // Get the player wrapper
                final BukkitPlayer bukkitPlayer = BukkitUtil.getPlayer(player);

                // Check if a player is in a region
                if (bukkitPlayer.isInRegion()) {

                    // Get the region the player is in
                    final Region r = bukkitPlayer.getRegion();

                    // Check if the player meta data is loaded
                    if (bukkitPlayer.getMeta() != null /* Is loaded */) {
                        if (!bukkitPlayer.getMeta().hasMeta("indicators")) {
                            continue;
                        }

                        //
                        // This caches the Y value of the player, so
                        // we don't have to send the indicators on each run
                        //
                        final int y = player.getLocation().getBlockY();
                        if (bukkitPlayer.getMeta().hasMeta("indicatorY")) {
                            int tempY = bukkitPlayer.getMeta().getInt("indicatorY");
                            if (y != tempY) {
                                bukkitPlayer.deleteIndicators();
                                bukkitPlayer.getMeta().setMeta("indicatorY", y);
                            } else {
                                continue;
                            }
                        } else {
                            bukkitPlayer.getMeta().setMeta("indicatorY", y);
                        }

                        /*
                        Get the type of indicator:


                        - corners: Only show the corners
                        - corners_outline: Show the corners in different colours (depending on their location relative to the
                                            reversed path), and the region outline in black
                        - outline: Show the region outline in  black
                        - rectangles: Outline the rectangles with random colours

                         */
                        final String type = bukkitPlayer.getMeta().getMeta("indicators", PlayerMeta.Parsers.stringParser);
                        switch (type) {
                            case "corners": {
                                final ImmutableList<Vector2> corners = r.getCorners();
                                for (int i = 0; i < corners.size(); i++) {
                                    Location location = BukkitUtil.vectorToLocation(player.getWorld(), corners.get(i), player.getLocation().getY() + 0.5d);
                                    location.add(0, -0.3d, 0);
                                    bukkitPlayer.showIndicator(location.getX(), location.getY(), location.getZ(),
                                            DyeColor.values()[i].name());
                                }
                            } break;
                            case "corners_outline": {
                                final ImmutableList<Vector2> corners = r.getCorners();
                                for (int i = 0; i < corners.size(); i++) {
                                    Location location = BukkitUtil.vectorToLocation(player.getWorld(), corners.get(i), player.getLocation().getY() + 0.5d);
                                    location.add(0, -0.3d, 0);
                                    bukkitPlayer.showIndicator(location.getX(), location.getY(), location.getZ(),
                                            DyeColor.values()[i].name());
                                }
                                for (final Vector2 vector2 : r.getOutline(false)) {
                                    Location location = BukkitUtil.vectorToLocation(player.getWorld(), vector2, player.getLocation().getY() + 0.5d);
                                    location.add(0, -0.3d, 0);
                                    bukkitPlayer.showIndicator(location.getX(), location.getY(), location.getZ(),
                                            DyeColor.BLACK.name());
                                }
                            } break;
                            case "outline": {
                                for (Vector2 vector2 : r.getOutline(true)) {
                                    Location location = BukkitUtil.vectorToLocation(player.getWorld(), vector2, player.getLocation().getY() + 0.5d);
                                    location.add(0, -0.3d, 0);
                                    bukkitPlayer.showIndicator(location.getX(), location.getY(), location.getZ(),
                                            DyeColor.BLACK.name());
                                }
                            } break;
                            case "rectangles": {
                                for (Rectangle re : r.getRectangles()) {
                                    for (Vector2 vector2 : re.getOutline()) {
                                        Location location = BukkitUtil.vectorToLocation(player.getWorld(), vector2, player.getLocation().getY() + 0.5d);
                                        location.add(0, 1.3d, 0);
                                        bukkitPlayer.showIndicator(location.getX(), location.getY(), location.getZ(),
                                                DyeColor.values()[(int)(Math.random() * DyeColor.values().length)].name());
                                    }
                                }
                            } break;
                            default: {
                                player.sendMessage("Illegal \"indicators\" type: " + type);
                                player.sendMessage("Deleting meta value");
                                bukkitPlayer.getMeta().removeMeta("indicators");
                            } break;
                        }
                    } else {
                        // Whoos: The meta wasn't loaded
                        bukkitPlayer.sendMessage("Your meta isn't loaded :/");
                    }
                } else {
                    // The player isn't in a region anymore, reset the indicator Y cache
                    // and remove all indicators
                    bukkitPlayer.getMeta().removeMeta("indicatorY");
                    bukkitPlayer.deleteIndicators();
                }
            }
        }, 0L, 20L);
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
    public PlayerManager getPlayerManager() {
        return this;
    }

    @Override
    public void runSyncDelayed(Runnable r, long time) {
        getServer().getScheduler().runTaskLater(this, r, time);
    }

    @Override
    public RectangularLogger info(String str) {
        getLogger().info(str);
        return this;
    }

    @Override
    public RectangularLogger warning(String msg) {
        getLogger().warning(msg);
        return this;
    }

    @Override
    public RectangularPlayer getPlayer(String username) {
        return BukkitUtil.getPlayer(Bukkit.getPlayer(username));
    }

    @Override
    public RectangularPlayer getPlayer(UUID uuid) {
        return BukkitUtil.getPlayer(Bukkit.getPlayer(uuid));
    }

    private Map<String, PlayerMeta> preFetched = new ConcurrentHashMap<>();

    @Override
    public PlayerMeta unloadMeta(UUID uuid) {
        if (preFetched.containsKey(uuid.toString())) {
            return preFetched.remove(uuid.toString());
        }
        return null;
    }

    @Override
    public void loadMeta(final UUID uuid) {
        Runnable runnable = () -> {
            preFetched.put(uuid.toString(), Rectangular.get().getDatabase().loadPlayerMeta(uuid));
        };
        if (Bukkit.isPrimaryThread()) {
            runAsync(runnable);
        } else {
            runnable.run();
        }
    }
}
