package com.intellectualsites.rectangular.player;

import com.intellectualsites.rectangular.core.Region;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PlayerEventObserver {

    @Getter
    private final RectangularPlayer player;

    public void onPlayerLeaveRegion(Region left) {
        player.sendMessage("&cYou left region: " + left.getId());
        // TODO: Throw event
    }

    public void onPlayerEnterRegion() {
        player.sendMessage("&cYou entered region: " + player.getRegion().getId());
        // TODO: Throw event
    }

}
