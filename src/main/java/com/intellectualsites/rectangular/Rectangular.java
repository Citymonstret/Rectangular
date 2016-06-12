package com.intellectualsites.rectangular;

import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.common.config.FileConfiguration;
import com.intellectualsites.rectangular.bukkit.RectangularPlugin;
import com.intellectualsites.rectangular.core.Rectangle;
import com.intellectualsites.rectangular.database.RectangularDB;
import com.intellectualsites.rectangular.database.RectangularDBMySQL;
import com.intellectualsites.rectangular.manager.ManagerProvider;
import com.intellectualsites.rectangular.manager.RegionManager;
import com.intellectualsites.rectangular.manager.WorldManager;
import com.sun.javafx.accessible.utils.Rect;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public final class Rectangular {

    private static Rectangular rectangular;

    public static void setup(ManagerProvider provider) throws IllegalAccessException {
        if (rectangular != null) {
            throw new IllegalAccessException("Cannot setup rectangular when already setup, duh");
        }
        rectangular = new Rectangular(provider);
    }

    public static Rectangular get() {
        return rectangular;
    }

    @Getter
    public RegionManager regionManager;

    @Getter
    public WorldManager worldManager;

    private Rectangular(ManagerProvider provider) {
        rectangular = this;

        FileConfiguration coreConfiguration = new FileConfiguration(JavaPlugin.getPlugin(RectangularPlugin.class), "core.yml");
        if (coreConfiguration.exists()) {
            coreConfiguration.load();
        } else {
            ConfigurationNode database = new ConfigurationNode();
            database.setHeader("Database Related Configuration");
            database.setHeader("username", "The mysql username");
            database.set("username", "root");
            database.setHeader("password", "The mysql password");
            database.set("password", "password");
            database.setHeader("port", "The mysql server port");
            database.set("port", 3306);
            database.setHeader("host", "The mysql server address");
            database.set("host", "localhost");
            database.setHeader("database", "The database name");
            database.set("database", "rectangular");
            database.setHeader("prefix", "The table prefix");
            database.set("prefix", "rect__");
            coreConfiguration.set("database", database);
            coreConfiguration.save();
        }

        // Yay
        ConfigurationNode dbNode = coreConfiguration.getNode("database");
        RectangularDB database = new RectangularDBMySQL(
                dbNode.get("username", String.class),
                dbNode.get("user", String.class),
                dbNode.get("password", String.class),
                dbNode.get("host", String.class),
                dbNode.get("port", Integer.class)
        );

        if (!database.schemaExists()) {
            database.createSchema();
        }

        this.worldManager = provider.getWorldManager();
        this.regionManager = new RegionManager(worldManager, database);
    }

}
