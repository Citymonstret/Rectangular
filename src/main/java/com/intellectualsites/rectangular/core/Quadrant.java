package com.intellectualsites.rectangular.core;

import com.intellectualsites.rectangular.vector.Vector2;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class Quadrant extends Rectangle {

    @Getter
    private final List<Integer> ids = new ArrayList<>();

    public Quadrant(final Vector2 min, final Vector2 max) {
        super(min, max);
    }

    public static Quadrant findQuadrant(Quadrant[] quadrants, float midX, float midY, Vector2 v2) {
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
        return quadrant;
    }

    public boolean isEmpty() {
        return ids.isEmpty();
    }
}
