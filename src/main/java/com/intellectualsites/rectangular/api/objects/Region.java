package com.intellectualsites.rectangular.api.objects;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.intellectualsites.rectangular.core.Rectangle;
import com.intellectualsites.rectangular.vector.Vector2;

import java.util.Collection;

public interface Region {

    void setRectangles(Collection<Rectangle> rectangles);

    void compile();

    boolean isInRegion(Vector2 v2);

    void compileCorners();

    ImmutableList<Vector2> getCorners();

    ImmutableCollection<Vector2> getOutline(boolean includeCorners);

    String getContainerID();

    Rectangle getBoundingBox();

    Rectangle[] getRectangles();

    com.intellectualsites.rectangular.core.Quadrant[] getQuadrants();

    int getWidth();

    int getHeight();

    float getMidX();

    float getMidY();

    int getId();

    String getOwningContainer();

    com.intellectualsites.rectangular.data.RegionData getData();

    void setData(com.intellectualsites.rectangular.data.RegionData data);

    boolean isCompiled();

    boolean overlaps(Rectangle r2);

    boolean isExpandableTo(Rectangle r2);

    Rectangle isExceeding(Rectangle r2);
}
