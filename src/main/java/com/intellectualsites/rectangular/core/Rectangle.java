package com.intellectualsites.rectangular.core;

import com.intellectualsites.rectangular.vector.Vector2;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.awt.geom.Area;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class Rectangle {

    @Getter
    private final Vector2 min, max;

    @Getter
    private int id;

    public Rectangle(final int id, final Vector2 min, final Vector2 max) {
        this.id = id;
        this.min = min;
        this.max = max;
    }

    public Rectangle() {
        this(new Vector2(), new Vector2());
    }

    public boolean isInside(@NonNull final Vector2 v2) {
        return v2.getX() >= min.getX() && v2.getX() <= max.getX()
                && v2.getY() >= min.getY() && v2.getY() <= max.getY();
    }

    public boolean overlaps(@NonNull final Rectangle re2) {
        Vector2 l1 = new Vector2(getMin().getX(), getMax().getY());
        Vector2 r1 = new Vector2(getMax().getX(), getMin().getY());
        Vector2 l2 = new Vector2(re2.getMin().getX(), re2.getMax().getY());
        Vector2 r2 = new Vector2(re2.getMax().getX(), re2.getMin().getY());
        return !(l1.getX() > r2.getX() || l2.getX() > r1.getX()) &&
                !(l1.getY() < r2.getY() || l2.getY() < r1.getY());
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

    public void copyFrom(@NonNull final Rectangle boundingBox) {
        this.min.set(boundingBox.getMin());
        this.max.set(boundingBox.getMax());
    }

    public void shrink(int i) {
        this.min.add(i, i);
        this.max.subtract(i, i);
    }

    public Area toArea(@NonNull final Vector2 min) {
        return new Area(new java.awt.Rectangle(getMin().getX() - min.getX(),
                getMin().getY() - min.getY(), getMax().getX() - getMin().getX(),
                getMax().getY() - getMin().getY()));
    }

    @Override
    public String toString() {
        return "Rectangle:[Min:[" + getMin().toString() + "],Max:[" + getMax().toString() + "]]";
    }
}
