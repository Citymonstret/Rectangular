package com.intellectualsites.rectangular.event.impl;

import com.intellectualsites.rectangular.api.objects.Region;
import com.intellectualsites.rectangular.event.PlayerEvent;
import com.intellectualsites.rectangular.player.RectangularPlayer;

public class PlayerEnteredRegionEvent extends PlayerEvent {

    public PlayerEnteredRegionEvent(final RectangularPlayer player) {
        super(player);
    }

    public Region getRegion() {
        return getPlayer().getRegion();
    }

}
