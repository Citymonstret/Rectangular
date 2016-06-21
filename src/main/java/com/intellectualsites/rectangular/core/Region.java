package com.intellectualsites.rectangular.core;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.intellectualsites.rectangular.data.RegionData;
import com.intellectualsites.rectangular.vector.Vector2;
import lombok.Getter;
import lombok.Setter;

import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.PathIterator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
    private ImmutableList<Vector2> corners;

    @Setter
    @Getter
    private RegionData data;

    public Region(int id, int level, String owningContainer) {
        super(level, null);

        this.id = id;
        this.owningContainer = owningContainer;
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

    private void compileCorners() {
        Area nativeArea = new Area();
        for (Rectangle rectangle : rectangles) {
            nativeArea.add(rectangle.toArea(boundingBox.getMin()));
        }
        List<Vector2> list = new ArrayList<>();
        double coords[] = new double[6];
        AffineTransform transform = AffineTransform.getRotateInstance(1,0);
        for (PathIterator pathIterator = nativeArea.getPathIterator(transform); !pathIterator.isDone(); pathIterator.next()) {
            int type = pathIterator.currentSegment(coords);
            list.add(new Vector2(boundingBox.getMin().getX() + (int) coords[0], boundingBox.getMin().getY() + (int) coords[1]));
        }
        this.corners = ImmutableList.copyOf(list);
    }

    public ImmutableList<Vector2> getCorners() {
        if (corners == null) {
            compileCorners();
        }
        return corners;
    }

    public ImmutableCollection<Vector2> getOutline(boolean includeCorners) {
        List<Vector2> points = new ArrayList<>();
        List<Vector2> toRemove = new ArrayList<>();
        for (Rectangle rectangle : rectangles) {
            for (Vector2 v2 : rectangle.getOutline()) {
                if (points.contains(v2)) {
                    toRemove.add(v2);
                } else {
                    points.add(v2);
                }
            }
        }
        if (includeCorners) {
            for (Vector2 v2 : getCorners()) {
                if (points.contains(v2)) {
                    toRemove.add(v2);
                } else {
                    points.add(v2);
                }
            }
        }
        points.removeAll(toRemove);
        return ImmutableList.copyOf(points);
    }

    @Override
    public String getContainerID() {
        return "r:" + id + ";l:" + getLevel();
    }
}
