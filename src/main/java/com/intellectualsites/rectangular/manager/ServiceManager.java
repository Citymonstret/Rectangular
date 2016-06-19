package com.intellectualsites.rectangular.manager;

import com.intellectualsites.rectangular.logging.RectangularLogger;
import com.intellectualsites.rectangular.selection.SelectionManager;

import java.io.File;

public interface ServiceManager {

    WorldManager getWorldManager();

    RectangularLogger logger();

    void shutdown(String reason);

    void runAsync(Runnable r);

    SelectionManager getSelectionManager();

    File getFolder();
}
