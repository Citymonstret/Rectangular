package com.intellectualsites.rectangular.bukkit;

import com.intellectualsites.rectangular.Rectangular;
import com.intellectualsites.rectangular.commands.MainCommand;
import com.intellectualsites.rectangular.commands.SubCommand;
import com.intellectualsites.rectangular.manager.ServiceManager;
import com.intellectualsites.rectangular.manager.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RectangularPlugin extends JavaPlugin implements ServiceManager {

    @Override
    public void onEnable() {

        List<SubCommand> subCommands = new ArrayList<>();

        getCommand("rectangular").setExecutor(new MainCommand(subCommands));
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
}
