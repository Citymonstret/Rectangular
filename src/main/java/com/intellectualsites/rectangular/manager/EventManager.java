package com.intellectualsites.rectangular.manager;

import com.google.common.eventbus.EventBus;
import com.intellectualsites.rectangular.CoreModule;
import com.intellectualsites.rectangular.event.RectangularEvent;
import com.intellectualsites.rectangular.event.RectangularListener;

public class EventManager implements CoreModule {

    private final EventBus eventBus;

    public EventManager() {
        this.eventBus = new EventBus("rectangular");
    }

    public void push(RectangularEvent event) {
        eventBus.post(event);
    }

    public void register(RectangularListener listener) {
        eventBus.register(listener);
    }
}
