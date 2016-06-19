package com.intellectualsites.rectangular.event;

import com.intellectualsites.rectangular.player.RectangularPlayer;
import lombok.Getter;

public abstract class PlayerEvent extends RectangularEvent {

    @Getter
    private final RectangularPlayer player;

    public PlayerEvent(RectangularPlayer player) {
        this.player = player;
    }

}
