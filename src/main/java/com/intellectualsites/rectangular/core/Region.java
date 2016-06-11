package com.intellectualsites.rectangular.core;

import com.intellectualsites.rectangular.vector.Vector2;

public class Region {

    private Rectangle boundingBox;
    private Rectangle[] rectangles;
    private Quadrant[] quadrants = new Quadrant[4];
    private int width, height;

    private float midX, midY;

    public Region() {
        this.boundingBox = new Rectangle();
        this.rectangles = new Rectangle[0];
    }

    private void compile() {
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

        // Calculate the width and height
        this.width = boundingBox.getMax().getX() - boundingBox.getMin().getX();
        this.height = boundingBox.getMax().getY() - boundingBox.getMin().getY();

        // Calculate midpoints (for quadrants)
        this.midX = this.boundingBox.getMin().getX() + (width / 2);
        this.midY = this.boundingBox.getMax().getY() + (height / 2);

        // First Quadrant
        {
            Vector2 min = new Vector2((int) midX, (int) midY);
            Vector2 max = this.boundingBox.getMax().clone();
            Quadrant quadrant = new Quadrant(min,max);
            for (int i = 0; i < rectangles.length; i++) {
                if (quadrant.overlaps(rectangles[i])) {
                    quadrant.getRectangles().add(i);
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
                    quadrant.getRectangles().add(i);
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
                    quadrant.getRectangles().add(i);
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
                    quadrant.getRectangles().add(i);
                }
            }
            this.quadrants[3] = quadrant;
        }
    }

    public boolean isInRegion(Vector2 v2) {
        if (boundingBox.isInside(v2)) {
            Quadrant quadrant;
            if (v2.getX() > midX) {
                if (v2.getY() > midY) {
                    quadrant = quadrants[0];
                } else {
                    quadrant = quadrants[1];
                }
            } else {
                if (v2.getY() < midY) {
                    quadrant = quadrants[3];
                } else {
                    quadrant = quadrants[4];
                }
            }
            for (int rectangleID : quadrant.getRectangles()) {
                if (rectangles[rectangleID].isInside(v2)) {
                    return true;
                }
            }
        }
        return false;
    }
}
