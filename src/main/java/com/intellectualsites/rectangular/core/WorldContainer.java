package com.intellectualsites.rectangular.core;

import com.intellectualsites.rectangular.vector.Vector2;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class WorldContainer extends RegionContainer {

    @Getter
    public final String worldName;

    public WorldContainer(String worldName) {
        super(0, new Rectangle(new Vector2(Integer.MIN_VALUE, Integer.MIN_VALUE), new Vector2(Integer.MAX_VALUE, Integer.MAX_VALUE)));
        this.worldName = worldName;
    }

    @Override
    public String getContainerID() {
        return generateID(getWorldName());
    }

    public static String generateID(String worldName) {
        return "w:" + worldName;
    }
}
