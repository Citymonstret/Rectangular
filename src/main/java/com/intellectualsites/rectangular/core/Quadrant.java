package com.intellectualsites.rectangular.core;

import com.intellectualsites.rectangular.vector.Vector2;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class Quadrant extends Rectangle {

    @Getter
    private final List<Integer> rectangles = new ArrayList<>();

    public Quadrant(final Vector2 min, final Vector2 max) {
        super(min, max);
    }
}
