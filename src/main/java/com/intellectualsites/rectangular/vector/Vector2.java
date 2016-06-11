package com.intellectualsites.rectangular.vector;

import lombok.Getter;
import lombok.NonNull;

/**
 * A very simple, straight to the point,
 * vector implementation made specifically
 * for this project
 *
 * @author Citymonstret
 */
public class Vector2 {

    @Getter
    int x, y;

    public Vector2(final int x, final int y) {
        this.x = x;
        this.y = y;
    }

    public Vector2(final int z) {
        this(z, z);
    }

    public Vector2(@NonNull final int[] array) {
        if (array.length < 2) {
            throw new IllegalArgumentException("Array must contain 2 int values");
        }
        this.x = array[0];
        this.y = array[1];
    }

    public Vector2() {
        this(0);
    }

    public Vector2(@NonNull final Vector2 v2) {
        this.x = v2.x;
        this.y = v2.y;
    }

    public int get(final int n) {
        switch (n) {
            case 0:
                return x;
            case 1:
                return y;
            default:
                return -1;
        }
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && obj instanceof Vector2 && equals((Vector2) obj);
    }

    public boolean equals(@NonNull final Vector2 v2) {
        return v2.x == x && v2.y == y;
    }

    public void set(@NonNull final Vector2 v2) {
        set(v2.x, v2.y);
    }

    public void set(final int x, final int y) {
        this.x = x;
        this.y = y;
    }

    public int dot(Vector2 v2) {
        return (x * v2.x) + (y * v2.y);
    }

    public Vector2 getPerpendicular() {
        return new Vector2(-y, x);
    }

    public Vector2 add(Vector2 v2) {
        return this.add(v2.x, v2.y);
    }

    public Vector2 add(final int x, final int y) {
        this.x += x;
        this.y += y;
        return this;
    }

    public Vector2 subtract(Vector2 v2) {
        return add(-v2.x, -v2.y);
    }

    public Vector2 subtract(int x, int y) {
        return add(-x, -y);
    }

    public Vector2 scale(int a) {
        this.x *= a;
        this.y *= a;
        return this;
    }

    public int distanceSquared(Vector2 v2) {
        int dx = v2.x - x;
        int dy = v2.y - y;
        return (int)(dx * dx) + (dy * dy);
    }

    public int distance(Vector2 v2) {
        return (int) Math.sqrt(distanceSquared(v2));
    }

    public Vector2 clone() {
        return new Vector2(this);
    }

    /**
     * Borrowed from Slick2D
     */
    @Override
    public int hashCode() {
        return 997 * ((int) x) ^ 991 * ((int) y);
    }
}
