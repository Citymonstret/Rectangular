package com.intellectualsites.rectangular.selection;

import com.intellectualsites.rectangular.player.RectangularPlayer;
import com.intellectualsites.rectangular.core.Rectangle;

@SuppressWarnings("unused")
public interface SelectionManager {

    boolean hasSelection(RectangularPlayer player);

    Rectangle getSelection(RectangularPlayer player);

    void clearSelection(RectangularPlayer player);

    boolean equipPlayer(RectangularPlayer player);

    boolean isFinished(RectangularPlayer player);

}
