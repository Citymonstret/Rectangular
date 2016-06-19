package com.intellectualsites.rectangular.event.impl;

import com.intellectualsites.rectangular.event.CoreEvent;
import com.intellectualsites.rectangular.manager.RegionManager;

public class RegionManagerDoneEvent extends CoreEvent<RegionManager> {

    public RegionManagerDoneEvent(RegionManager module) {
        super(module);
    }

}
