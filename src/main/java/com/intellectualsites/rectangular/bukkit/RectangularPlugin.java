package com.intellectualsites.rectangular.bukkit;

import com.intellectualsites.rectangular.Rectangular;
import com.intellectualsites.rectangular.commands.MainCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class RectangularPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        getCommand("rectangular").setExecutor(new MainCommand());
        try {
            Rectangular.setup();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}
