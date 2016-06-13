package com.intellectualsites.rectangular;

import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.common.config.FileConfiguration;
import com.intellectualsites.rectangular.bukkit.RectangularPlugin;
import com.intellectualsites.rectangular.database.RectangularDB;
import com.intellectualsites.rectangular.database.RectangularDBMySQL;
import com.intellectualsites.rectangular.manager.ServiceManager;
import com.intellectualsites.rectangular.manager.RegionManager;
import com.intellectualsites.rectangular.manager.WorldManager;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public final class Rectangular {

    private static Rectangular rectangular;

    public static void setup(ServiceManager provider) throws IllegalAccessException {
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

    private Rectangular(ServiceManager provider) {
        rectangular = this;

        FileConfiguration coreConfiguration = new FileConfiguration(JavaPlugin.getPlugin(RectangularPlugin.class), "core.yml");
        if (coreConfiguration.exists()) {
            coreConfiguration.load();
        } else {
            ConfigurationNode database = coreConfiguration.getNode("database");
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
            coreConfiguration.save();
        }

        // Yay
        ConfigurationNode dbNode = coreConfiguration.getNode("database");
        RectangularDB database = new RectangularDBMySQL(
                dbNode.get("database", String.class),
                dbNode.get("username", String.class),
                dbNode.get("password", String.class),
                dbNode.get("host", String.class),
                dbNode.get("port", Integer.class),
                dbNode.get("prefix", String.class)
        );

        if (!database.testConnection()) {
            provider.shutdown("Couldn't connect to MySQL");
            return; // Not even needed, but keeping it there anyhow
        }

        if (!database.schemaExists()) {
            database.createSchema();
        }

        this.worldManager = provider.getWorldManager();
        this.regionManager = new RegionManager(worldManager, database);
    }

}
