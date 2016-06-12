package com.intellectualsites.rectangular.core;

import com.intellectualsites.rectangular.vector.Vector2;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class Rectangle {

    @Getter
    private final Vector2 min, max;

    public Rectangle() {
        this(new Vector2(), new Vector2());
    }

    public boolean isInside(Vector2 v2) {
        return v2.getX() >= min.getX() && v2.getX() <= max.getX()
                && v2.getY() >= min.getY() && v2.getY() <= max.getY();
    }

    public boolean overlaps(Rectangle r2) {
        Vector2 p1 = max, p2 = min, p3 = r2.max, p4 = r2.min;
        return !(p2.getY() < p3.getY() || p1.getY() > p4.getY() || p2.getX() < p3.getX() || p1.getX() > p4.getX());
    }

    public List<Vector2> getOutline() {
        List<Vector2> coordinates = new ArrayList<>();
        for (int y = min.getY(); y <= max.getY(); y++) {
            coordinates.add(new Vector2(min.getX(), y));
            coordinates.add(new Vector2(max.getX(), y));
        }
        for (int x = min.getX() + 1; x <= max.getX() - 1; x++) {
            coordinates.add(new Vector2(x, min.getY()));
            coordinates.add(new Vector2(x, max.getY()));
        }
        return coordinates;
    }

    public void copyFrom(Rectangle boundingBox) {
        this.min.set(boundingBox.getMin());
        this.max.set(boundingBox.getMax());
    }
}
