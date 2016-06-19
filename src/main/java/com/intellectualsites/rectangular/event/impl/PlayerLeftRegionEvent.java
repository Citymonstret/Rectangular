package com.intellectualsites.rectangular.event.impl;


import com.intellectualsites.rectangular.core.Region;
import com.intellectualsites.rectangular.event.PlayerEvent;
import com.intellectualsites.rectangular.player.RectangularPlayer;
import lombok.Getter;

public class PlayerLeftRegionEvent extends PlayerEvent {

    @Getter
    private final Region oldRegion;

    public PlayerLeftRegionEvent(final RectangularPlayer player, final Region oldRegion) {
        super(player);
        this.oldRegion = oldRegion;
    }

}
