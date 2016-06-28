package com.intellectualsites.rectangular.player;

import com.intellectualsites.rectangular.Rectangular;
import com.intellectualsites.rectangular.api.objects.Region;
import com.intellectualsites.rectangular.event.impl.PlayerEnteredRegionEvent;
import com.intellectualsites.rectangular.event.impl.PlayerLeftRegionEvent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PlayerEventObserver {

    @Getter
    private final RectangularPlayer player;

    public void onPlayerLeaveRegion(Region left) {
        Rectangular.getEventManager().push(new PlayerLeftRegionEvent(player, left));
    }

    public void onPlayerEnterRegion() {
        Rectangular.getEventManager().push(new PlayerEnteredRegionEvent(player));
    }

}
