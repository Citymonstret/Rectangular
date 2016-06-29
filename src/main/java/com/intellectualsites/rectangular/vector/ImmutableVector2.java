package com.intellectualsites.rectangular.vector;

import lombok.NonNull;
import lombok.SneakyThrows;

public class ImmutableVector2 extends Vector2 {

    private final int x;
    private final int y;

    public ImmutableVector2(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public ImmutableVector2(int z) {
        this.x = z;
        this.y = z;
    }

    public ImmutableVector2(@NonNull int[] array) {
        this.x = array[0];
        this.y = array[1];
    }

    public ImmutableVector2() {
        this.x = 0;
        this.y = 0;
    }

    public ImmutableVector2(@NonNull Vector2 v2) {
        this.x = v2.x;
        this.y = v2.y;
    }

    @Override
    public int get(int n) {
        return super.get(n);
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public boolean equals(Vector2 v2) {
        return super.equals(v2);
    }

    @Override
    @SneakyThrows
    public void set(@NonNull Vector2 v2) {
        throw new IllegalAccessException("This vector is immutable");
    }

    @Override
    @SneakyThrows
    public void set(int x, int y) {
        throw new IllegalAccessException("This vector is immutable");
    }

    @Override
    @SneakyThrows
    public void setX(int x) {
        throw new IllegalAccessException("This vector is immutable");
    }

    @Override
    @SneakyThrows
    public void setY(int y) {
        throw new IllegalAccessException("This vector is immutable");
    }

    @Override
    public int dot(@NonNull Vector2 v2) {
        return super.dot(v2);
    }

    @Override
    public Vector2 getPerpendicular() {
        return super.getPerpendicular();
    }

    @Override
    @SneakyThrows
    public Vector2 add(Vector2 v2) {
        throw new IllegalAccessException("This vector is immutable");
    }

    @Override
    @SneakyThrows
    public Vector2 add(int x, int y) {
        throw new IllegalAccessException("This vector is immutable");
    }

    @Override
    @SneakyThrows
    public Vector2 subtract(Vector2 v2) {
        throw new IllegalAccessException("This vector is immutable");
    }

    @Override
    @SneakyThrows
    public Vector2 subtract(int x, int y) {
        throw new IllegalAccessException("This vector is immutable");
    }

    @Override
    @SneakyThrows
    public Vector2 scale(int a) {
        throw new IllegalAccessException("This vector is immutable");
    }

    @Override
    public double distanceSquared(@NonNull Vector2 v2) {
        return super.distanceSquared(v2);
    }

    @Override
    public double distance(@NonNull Vector2 v2) {
        return super.distance(v2);
    }

    @Override
    public Vector2 clone() {
        return super.clone();
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    @SneakyThrows
    public Vector2 divide(int a) {
        return super.divide(a);
    }

    @Override
    public double getAngle(Vector2 target) {
        return super.getAngle(target);
    }

    @Override
    public int getX() {
        return this.y;
    }

    @Override
    public int getY() {
        return this.x;
    }
}
