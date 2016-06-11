package com.intellectualsites.rectangular.vector;

import lombok.Getter;
import lombok.NonNull;

import java.util.Vector;

/**
 * A very simple, straight to the point,
 * vector implementation made specifically
 * for this project
 *
 * @author Citymonstret
 */
public class Vector2f {

    @Getter
    float x, y;

    public Vector2f(final float x, final float y) {
        this.x = x;
        this.y = y;
    }

    public Vector2f(final float z) {
        this(z, z);
    }

    public Vector2f(@NonNull final float[] array) {
        if (array.length < 2) {
            throw new IllegalArgumentException("Array must contain 2 float values");
        }
        this.x = array[0];
        this.y = array[1];
    }

    public Vector2f() {
        this(0f);
    }

    public Vector2f(@NonNull final Vector2f v2) {
        this.x = v2.x;
        this.y = v2.y;
    }

    public float get(final int n) {
        switch (n) {
            case 0:
                return x;
            case 1:
                return y;
            default:
                return -1f;
        }
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && obj instanceof Vector2f && equals((Vector2f) obj);
    }

    public boolean equals(@NonNull final Vector2f v2) {
        return v2.x == x && v2.y == y;
    }

    public void set(@NonNull final Vector2f v2) {
        set(v2.x, v2.y);
    }

    public void set(final float x, final float y) {
        this.x = x;
        this.y = y;
    }

    public float dot(Vector2f v2) {
        return (x * v2.x) + (y * v2.y);
    }

    public Vector2f getPerpendicular() {
        return new Vector2f(-y, x);
    }

    public Vector2f add(Vector2f v2) {
        return this.add(v2.x, v2.y);
    }

    public Vector2f add(final float x, final float y) {
        this.x += x;
        this.y += y;
        return this;
    }

    public Vector2f subtract(Vector2f v2) {
        return add(-v2.x, -v2.y);
    }

    public Vector2f subtract(float x, float y) {
        return add(-x, -y);
    }

    public Vector2f scale(float a) {
        this.x *= a;
        this.y *= a;
        return this;
    }

    public float distanceSquared(Vector2f v2) {
        float dx = v2.x - x;
        float dy = v2.y - y;
        return (float)(dx * dx) + (dy * dy);
    }

    public float distance(Vector2f v2) {
        return (float) Math.sqrt(distanceSquared(v2));
    }

    /**
     * Borrowed from Slick2D
     */
    @Override
    public int hashCode() {
        return 997 * ((int) x) ^ 991 * ((int) y);
    }
}
