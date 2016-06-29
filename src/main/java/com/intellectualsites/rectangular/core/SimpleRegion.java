package com.intellectualsites.rectangular.core;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.intellectualsites.rectangular.api.objects.Region;
import com.intellectualsites.rectangular.data.RegionData;
import com.intellectualsites.rectangular.vector.Vector2;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.val;

import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.PathIterator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SimpleRegion extends RegionContainer implements Region {

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

    private ImmutableList<Vector2> corners;

    private boolean compiled = false;

    @Setter
    @Getter
    private RegionData data;

    public SimpleRegion(final int id, final int level, final String owningContainer) {
        super(level, null);
        this.id = id;
        this.owningContainer = owningContainer;
    }

    @Override
    public void setRectangles(@NonNull final Collection<Rectangle> rectangles) {
        this.rectangles = rectangles.toArray(new Rectangle[rectangles.size()]);
    }

    @Override
    public void compile() {
        // Initial values are just crayyyy
        int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE,
                maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE;

        // Calculate the bounds
        for (final Rectangle r : rectangles) {
            minX = Math.min(minX, r.getMin().getX());
            minY = Math.min(minY, r.getMin().getY());
            maxY = Math.max(maxY, r.getMax().getY());
            maxX = Math.max(maxX, r.getMax().getX());
        }

        // Update the bounds
        this.boundingBox = new Rectangle(new Vector2(minX, minY), new Vector2(maxX, maxY));
        super.getBounds().copyFrom(this.boundingBox); // Will update the region container

        // Calculate the width and height
        this.width = boundingBox.getMax().getX() - boundingBox.getMin().getX();
        this.height = boundingBox.getMax().getY() - boundingBox.getMin().getY();

        // Calculate midpoints (for quadrants)
        this.midX = this.boundingBox.getMin().getX() + (width / 2);
        this.midY = this.boundingBox.getMin().getY() + (height / 2);

        // First Quadrant
        {
            final Vector2 min = new Vector2((int) midX, (int) midY);
            final Vector2 max = this.boundingBox.getMax().clone();
            final Quadrant quadrant = new Quadrant(min,max);
            for (int i = 0; i < rectangles.length; i++) {
                if (quadrant.overlaps(rectangles[i])) {
                    quadrant.getIds().add(i);
                }
            }
            this.quadrants[0] = quadrant;
        }
        // Second Quadrant
        {
            final Vector2 min = new Vector2((int) midX, boundingBox.getMin().getY());
            final Vector2 max = new Vector2(boundingBox.getMax().getX(), (int) midY);
            final Quadrant quadrant = new Quadrant(min,max);
            for (int i = 0; i < rectangles.length; i++) {
                if (quadrant.overlaps(rectangles[i])) {
                    quadrant.getIds().add(i);
                }
            }
            this.quadrants[1] = quadrant;
        }
        // Third Quadrant
        {
            final Vector2 min = this.boundingBox.getMin().clone();
            final Vector2 max = new Vector2((int) midX, (int) midY);
            final Quadrant quadrant = new Quadrant(min,max);
            for (int i = 0; i < rectangles.length; i++) {
                if (quadrant.overlaps(rectangles[i])) {
                    quadrant.getIds().add(i);
                }
            }
            this.quadrants[2] = quadrant;
        }
        // Fourth Quadrant
        {
            final Vector2 min = new Vector2(boundingBox.getMin().getX(), (int) midY);
            final Vector2 max = new Vector2((int) midX, boundingBox.getMax().getY());
            final Quadrant quadrant = new Quadrant(min,max);
            for (int i = 0; i < rectangles.length; i++) {
                if (quadrant.overlaps(rectangles[i])) {
                    quadrant.getIds().add(i);
                }
            }
            this.quadrants[3] = quadrant;
        }

        this.compiled = true;
        this.compileCorners();
    }

    @Override
    public boolean isInRegion(@NonNull final Vector2 v2) {
        if (this.boundingBox.isInside(v2)) {
            final Quadrant quadrant = Quadrant.findQuadrant(quadrants, midX, midY, v2);
            for (final int rectangleID : quadrant.getIds()) {
                if (this.rectangles[rectangleID].isInside(v2)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void compileCorners() {
        final Area nativeArea = new Area();
        for (final Rectangle rectangle : rectangles) {
            nativeArea.add(rectangle.toArea(boundingBox.getMin()));
        }
        final List<Vector2> list = new ArrayList<>();
        final double coords[] = new double[6];
        final AffineTransform transform = AffineTransform.getRotateInstance(1,0);
        for (PathIterator pathIterator = nativeArea.getPathIterator(transform); !pathIterator.isDone(); pathIterator.next()) {
            pathIterator.currentSegment(coords);
            list.add(new Vector2(boundingBox.getMin().getX() + (int) coords[0], boundingBox.getMin().getY() + (int) coords[1]));
        }
        this.corners = ImmutableList.copyOf(list);
    }

    @Override
    public ImmutableList<Vector2> getCorners() {
        return corners;
    }

    @Override
    public ImmutableCollection<Vector2> getOutline(final boolean includeCorners) {
        final List<Vector2> points = new ArrayList<>();
        final List<Vector2> toRemove = new ArrayList<>();
        for (val rectangle : rectangles) {
            for (val v2 : rectangle.getOutline()) {
                if (points.contains(v2)) {
                    toRemove.add(v2);
                } else {
                    points.add(v2);
                }
            }
        }
        if (includeCorners) {
            for (val v2 : getCorners()) {
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

    @Override
    public boolean isCompiled() {
        return this.compiled;
    }

    @Override
    public boolean overlaps(@NonNull final Rectangle r2) {
        for (Rectangle rectangle : rectangles) {
            if (rectangle.overlaps(r2)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isExpandableTo(@NonNull final Rectangle r2) {
        for (Rectangle rectangle : rectangles) {
            if (rectangle.getMax().getY() == r2.getMin().getY() ||
                    rectangle.getMax().getX() == r2.getMin().getX() ||
                    rectangle.getMin().getY() == r2.getMax().getY() ||
                    rectangle.getMin().getX() == r2.getMax().getX()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Rectangle isExceeding(Rectangle r2) {
        Vector2[] r2c = r2.getCorners();
        for (Rectangle rectangle : rectangles) {
            boolean r2c0 = rectangle.isInside(r2c[0]);
            boolean r2c1 = rectangle.isInside(r2c[1]);
            boolean r2c2 = rectangle.isInside(r2c[2]);
            boolean r2c3 = rectangle.isInside(r2c[3]);
            if (r2c0 && r2c1 && !r2c2 && !r2c3) {
                r2.getMax().setX(rectangle.getMin().getX());
            } else if (r2c1 && r2c2 && !r2c0 && !r2c3) {
                r2.getMin().setY(rectangle.getMax().getY());
            } else if (r2c2 && r2c3 && !r2c0 && !r2c1) {
                r2.getMin().setX(rectangle.getMax().getX());
            } else if (r2c3 && r2c0 && !r2c2 && !r2c1) {
                r2.getMax().setY(rectangle.getMin().getY());
            } else {
                continue;
            }
            return r2;
        }
        return null;
    }

    @Override
    public int getRectangle(Vector2 location) {
        final Quadrant currentQuadrant = Quadrant.findQuadrant(quadrants, midX, midY, location);
        for (final int rectangleID : currentQuadrant.getIds()) {
            if (this.rectangles[rectangleID].isInside(location)) {
                return rectangleID;
            }
        }
        return -1;
    }

    @Override
    public String toString() {
        return Integer.toString(this.getId());
    }
}
