package com.intellectualsites.rectangular.core;

import com.intellectualsites.rectangular.data.RegionData;
import com.intellectualsites.rectangular.vector.Vector2;
import lombok.Getter;

import java.util.Collection;

public class Region extends RegionContainer {

    @Getter
    private Rectangle boundingBox;

    @Getter
    private Rectangle[] rectangles;

    @Getter
    private Quadrant[] quadrants = new Quadrant[4];

    @Getter
    private int width, height;

    @Getter
    private float midX, midY;

    @Getter
    private final int id;

    @Getter
    private final String owningContainer;

    @Getter
    private RegionData data;

    public Region(int id, int level, String owningContainer) {
        super(level, null);

        this.id = id;
        this.owningContainer = owningContainer;

        // TODO: Load data
        this.data = new RegionData();
    }

    public void setRectangles(Collection<Rectangle> rectangles) {
        this.rectangles = rectangles.toArray(new Rectangle[rectangles.size()]);
    }

    public void compile() {
        // Initial values are just crayyyy
        int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE,
                maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE;

        // Calculate the bounds
        for (final Rectangle r : rectangles) {
            if (r.getMin().getX() < minX) {
                minX = r.getMin().getX();
            }
            if (r.getMin().getY() < minY) {
                minY = r.getMin().getY();
            }
            if (r.getMax().getY() > maxY) {
                maxY = r.getMax().getY();
            }
            if (r.getMax().getX() > maxX) {
                maxX = r.getMax().getX();
            }
        }
        this.boundingBox = new Rectangle(new Vector2(minX, minY), new Vector2(maxX, maxY));
        super.getBounds().copyFrom(this.boundingBox); // Will update the region container

        // Calculate the width and height
        this.width = boundingBox.getMax().getX() - boundingBox.getMin().getX();
        this.height = boundingBox.getMax().getY() - boundingBox.getMin().getY();

        // Calculate midpoints (for quadrants)
        this.midX = this.boundingBox.getMin().getX() + (width / 2);
        this.midY = this.boundingBox.getMin().getY() + (height / 2);

        //
        // TODO: Instructions below
        //
        // Make it so that each quadrant may contain
        // its own quadrants, if there are more than N
        // number of rectangles within the quadrant, to
        // speedup the looping
        //

        // First Quadrant
        {
            Vector2 min = new Vector2((int) midX, (int) midY);
            Vector2 max = this.boundingBox.getMax().clone();
            Quadrant quadrant = new Quadrant(min,max);
            for (int i = 0; i < rectangles.length; i++) {
                if (quadrant.overlaps(rectangles[i])) {
                    quadrant.getIds().add(i);
                }
            }
            this.quadrants[0] = quadrant;
        }
        // Second Quadrant
        {
            Vector2 min = new Vector2((int) midX, boundingBox.getMin().getY());
            Vector2 max = new Vector2(boundingBox.getMax().getX(), (int) midY);
            Quadrant quadrant = new Quadrant(min,max);
            for (int i = 0; i < rectangles.length; i++) {
                if (quadrant.overlaps(rectangles[i])) {
                    quadrant.getIds().add(i);
                }
            }
            this.quadrants[1] = quadrant;
        }
        // Third Quadrant
        {
            Vector2 min = this.boundingBox.getMin().clone();
            Vector2 max = new Vector2((int) midX, (int) midY);
            Quadrant quadrant = new Quadrant(min,max);
            for (int i = 0; i < rectangles.length; i++) {
                if (quadrant.overlaps(rectangles[i])) {
                    quadrant.getIds().add(i);
                }
            }
            this.quadrants[2] = quadrant;
        }
        // Fourth Quadrant
        {
            Vector2 min = new Vector2(boundingBox.getMin().getX(), (int) midY);
            Vector2 max = new Vector2((int) midX, boundingBox.getMax().getY());
            Quadrant quadrant = new Quadrant(min,max);
            for (int i = 0; i < rectangles.length; i++) {
                if (quadrant.overlaps(rectangles[i])) {
                    quadrant.getIds().add(i);
                }
            }
            this.quadrants[3] = quadrant;
        }
    }

    public boolean isInRegion(Vector2 v2) {
        if (boundingBox.isInside(v2)) {
            Quadrant quadrant = Quadrant.findQuadrant(quadrants, midX, midY, v2);
            for (int rectangleID : quadrant.getIds()) {
                if (rectangles[rectangleID].isInside(v2)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public String getContainerID() {
        return "r:" + id + ";l:" + getLevel();
    }
}
