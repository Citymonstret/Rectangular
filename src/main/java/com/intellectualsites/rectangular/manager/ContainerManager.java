package com.intellectualsites.rectangular.manager;

import com.intellectualsites.rectangular.CoreModule;
import com.intellectualsites.rectangular.core.ContainerFactory;
import com.intellectualsites.rectangular.core.RegionContainer;

import java.util.HashMap;
import java.util.Map;

public class ContainerManager implements CoreModule {

    private Map<Character, ContainerFactory<?>> containerFactories = new HashMap<>();

    public void addContainerFactory(ContainerFactory<?> factory) {
        containerFactories.put(factory.getRawPrefix(), factory);
    }

    public ContainerFactory<?> getContainerFactory(String key) {
        if (key.length() > 2) { // Such as: w:test_world
            String prefix = key.substring(0, 2);
            return getContainerFactory(prefix);
        }
        return getContainerFactory(key.toCharArray()[0]);
    }

    public ContainerFactory<?> getContainerFactory(char key) {
        return containerFactories.get(key);
    }

    public RegionContainer getRegionContainer(char prefix, String key) {
        return getContainerFactory(prefix).getContainer(key);
    }

    public RegionContainer getRegionContainer(String key) {
        String prefix = key.substring(0, 1);
        return getRegionContainer(prefix.toCharArray()[0], key.substring(2, key.length()));
    }
}
