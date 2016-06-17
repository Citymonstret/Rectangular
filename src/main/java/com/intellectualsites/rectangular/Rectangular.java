package com.intellectualsites.rectangular;

import com.intellectualsites.rectangular.database.RectangularDB;
import com.intellectualsites.rectangular.database.RectangularDBMySQL;
import com.intellectualsites.rectangular.manager.RegionManager;
import com.intellectualsites.rectangular.manager.ServiceManager;
import com.intellectualsites.rectangular.manager.WorldManager;
import lombok.Getter;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

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
    private ServiceManager serviceManager;

    @Getter
    private RegionManager regionManager;

    @Getter
    private WorldManager worldManager;

    @Getter
    private RectangularDB database;

    private Rectangular(ServiceManager provider) {
        rectangular = this;

        this.serviceManager = provider;

        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(new File(provider.getFolder(), "core.yml"));
        Configuration defaults = new MemoryConfiguration();
        ConfigurationSection database = defaults.createSection("database");
        database.set("username", "root");
        database.set("password", "password");
        database.set("port", 3306);
        database.set("host", "localhost");
        database.set("prefix", "rect__");
        yamlConfiguration.setDefaults(defaults);
        try {
            yamlConfiguration.save(new File(provider.getFolder(), "core.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }


        database = yamlConfiguration.getConfigurationSection("database");

        RectangularDB db = new RectangularDBMySQL(
                database.getString("database"),
                database.getString("username"),
                database.getString("password"),
                database.getString("host"),
                database.getInt("port"),
                database.getString("prefix")
        );

        if (!db.testConnection()) {
            provider.shutdown("Couldn't connect to MySQL");
            return; // Not even needed, but keeping it there anyhow
        }

        if (!db.schemaExists()) {
            db.createSchema();
        }

        this.database = db;
        this.worldManager = provider.getWorldManager();
        this.regionManager = new RegionManager(worldManager, db);

        provider.runAsync(() -> regionManager.load());
    }

}
