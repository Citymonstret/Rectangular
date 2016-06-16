package com.intellectualsites.rectangular.core;

import com.intellectualsites.rectangular.vector.Vector2;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class WorldContainer extends RegionContainer {

    @Getter
    public final String worldName;

    public WorldContainer(String worldName) {
        super(0, new Rectangle(new Vector2(Integer.MIN_VALUE, Integer.MIN_VALUE), new Vector2(Integer.MAX_VALUE, Integer.MAX_VALUE)));
        this.worldName = worldName;
        this.compileRegionContainer();
    }

    @Override
    public String getContainerID() {
        return generateID(getWorldName());
    }

    public static String generateID(String worldName) {
        if (worldName.startsWith("w:")) {
            return worldName;
        }
        return "w:" + worldName;
    }

    @Override
    public List<Integer> getRegionIDs() {
        List<Integer> ids = new ArrayList<>();
        for (Quadrant quadrant : getContainerQuadrants()) {
            ids.addAll(quadrant.getIds().stream().filter(id -> !ids.contains(id))
                    .collect(Collectors.toSet()));
        }
        return ids;
    }
}
