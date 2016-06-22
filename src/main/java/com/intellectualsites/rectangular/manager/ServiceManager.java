package com.intellectualsites.rectangular.manager;

import com.intellectualsites.rectangular.CoreModule;
import com.intellectualsites.rectangular.logging.RectangularLogger;
import com.intellectualsites.rectangular.selection.SelectionManager;

import java.io.File;

/**
 * This is a hack-opt version of a plugin implementation.
 * Rather than extending Rectangular, the implementation
 * provides Rectangular with a bunch of utilities
 * and methods, via this interface
 */
@SuppressWarnings("unused")
public interface ServiceManager extends CoreModule {

    WorldManager getWorldManager();

    RectangularLogger logger();

    /**
     * Shut down the plugin, after printing
     * the reason
     * @param reason Reason to shutdown
     */
    void shutdown(String reason);

    /**
     * Run an async task
     * @param r Task
     */
    void runAsync(Runnable r);

    /**
     * Run a synchronized task
     * @param r Task
     */
    void runSync(Runnable r);

    SelectionManager getSelectionManager();

    /**
     * Get the plugin data folder
     * @return Data folder
     */
    File getFolder();

    PlayerManager getPlayerManager();

    /**
     * Run a task after a specified amount of ticks
     * @param r Task
     * @param time Ticks before the task should run
     */
    void runSyncDelayed(Runnable r, long time);
}
