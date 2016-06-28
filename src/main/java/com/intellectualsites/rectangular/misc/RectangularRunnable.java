package com.intellectualsites.rectangular.misc;

import com.intellectualsites.rectangular.Rectangular;

@FunctionalInterface
public interface RectangularRunnable extends Runnable {


    default void runSync() {
        Rectangular.get().getServiceManager().runSync(this);
    }

    default void runAsync() {
        Rectangular.get().getServiceManager().runAsync(this);
    }

}
