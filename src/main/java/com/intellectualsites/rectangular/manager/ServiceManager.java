package com.intellectualsites.rectangular.manager;

public interface ServiceManager {

    WorldManager getWorldManager();

    void shutdown(String reason);

}
