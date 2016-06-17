package com.intellectualsites.rectangular.selection;

import com.intellectualsites.rectangular.player.RectangularPlayer;
import com.intellectualsites.rectangular.core.Rectangle;

public interface SelectionManager {

    boolean hasSelection(RectangularPlayer player);

    Rectangle getSelection(RectangularPlayer player);

    void clearSelection(RectangularPlayer player);

    void equipPlayer(RectangularPlayer player);

}
