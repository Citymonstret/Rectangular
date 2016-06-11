package core;

import com.intellectualsites.rectangular.vector.Vector2;
import com.intellectualsites.rectangular.vector.Vector2f;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

public class Quadrant extends Rectangle {

    @Getter
    private final List<Integer> rectangles = new ArrayList<>();

    public Quadrant(final Vector2 min, final Vector2 max) {
        super(min, max);
    }
}
