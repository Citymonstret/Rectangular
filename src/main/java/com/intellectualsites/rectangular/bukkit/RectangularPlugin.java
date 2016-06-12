package com.intellectualsites.rectangular.bukkit;

import com.intellectualsites.rectangular.Rectangular;
import com.intellectualsites.rectangular.commands.MainCommand;
import com.intellectualsites.rectangular.manager.ManagerProvider;
import com.intellectualsites.rectangular.manager.RegionManager;
import com.intellectualsites.rectangular.manager.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;

public class RectangularPlugin extends JavaPlugin implements ManagerProvider {

    @Override
    public void onEnable() {
        getCommand("rectangular").setExecutor(new MainCommand());
        try {
            Rectangular.setup();
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
}
