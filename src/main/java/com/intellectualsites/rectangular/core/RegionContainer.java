package com.intellectualsites.rectangular.core;

import com.intellectualsites.rectangular.vector.Vector2;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public abstract class RegionContainer {

    @Getter
    private final List<Integer> regionIDs = new ArrayList<>();

    @Getter
    private final Quadrant[] containerQuadrants = new Quadrant[4];

    @Getter
    private final int level;

    @Getter
    private final Rectangle bounds;

    private float width, height, midX, midY;

    public RegionContainer(final int level, final Rectangle bounds) {
        this.level = level;
        if (bounds == null) {
            this.bounds = new Rectangle();
        } else {
            this.bounds = bounds;
        }
    }

    public void compileRegionContainer() {
        width = bounds.getMax().getX() - bounds.getMin().getX();
        height = bounds.getMax().getY() - bounds.getMin().getY();

        midX = this.bounds.getMin().getX() + (width / 2);
        midY = this.bounds.getMin().getY() + (height / 2);


        // First Quadrant
        {
            Vector2 min = new Vector2((int) midX, (int) midY);
            Vector2 max = this.bounds.getMax().clone();
            Quadrant quadrant = new Quadrant(min,max);
            this.containerQuadrants[0] = quadrant;
        }
        // Second Quadrant
        {
            Vector2 min = new Vector2((int) midX, bounds.getMin().getY());
            Vector2 max = new Vector2(bounds.getMax().getX(), (int) midY);
            Quadrant quadrant = new Quadrant(min,max);
            this.containerQuadrants[1] = quadrant;
        }
        // Third Quadrant
        {
            Vector2 min = this.bounds.getMin().clone();
            Vector2 max = new Vector2((int) midX, (int) midY);
            Quadrant quadrant = new Quadrant(min,max);
            this.containerQuadrants[2] = quadrant;
        }
        // Fourth Quadrant
        {
            Vector2 min = new Vector2(bounds.getMin().getX(), (int) midY);
            Vector2 max = new Vector2((int) midX, bounds.getMax().getY());
            Quadrant quadrant = new Quadrant(min,max);
            this.containerQuadrants[3] = quadrant;
        }
    }

    public void compileQuadrants(final Region region) {
        if (regionIDs.contains(region.getId())) {
            return;
        }

        Bukkit.broadcastMessage("Trying to see if the region fits into the quadrant: " + region.getId());
        Bukkit.broadcastMessage("Region bounding box: " + region.getBoundingBox().getMin().toString());

        for (int i = 0; i < containerQuadrants.length; i++) {
            Quadrant quadrant = containerQuadrants[i];
            Bukkit.broadcastMessage("Quadrant bounding box: " + quadrant.toString());
            if (quadrant.overlaps(region.getBoundingBox())) {
                quadrant.getIds().add(region.getId());
                Bukkit.broadcastMessage("Added region " + region.getId() + " to quadrant: " + i);
            }
        }

        regionIDs.add(region.getId());
    }

    public void compileQuadrants(final Set<Region> regions) {
        regions.forEach(this::compileQuadrants);
    }

    public Quadrant getContainerQuadrant(Vector2 v2) {
        return Quadrant.findQuadrant(containerQuadrants, midX, midY, v2);
    }

    public boolean hasRegions() {
        return !regionIDs.isEmpty();
    }

    public abstract String getContainerID();


}
