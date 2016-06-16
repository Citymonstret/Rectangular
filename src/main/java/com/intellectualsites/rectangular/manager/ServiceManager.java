package com.intellectualsites.rectangular.manager;

import java.io.File;

public interface ServiceManager {

    WorldManager getWorldManager();

    void shutdown(String reason);

    void runAsync(Runnable r);

    File getFolder();
}
