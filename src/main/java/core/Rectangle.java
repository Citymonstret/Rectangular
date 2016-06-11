package core;

import com.intellectualsites.rectangular.vector.Vector2;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

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
}
