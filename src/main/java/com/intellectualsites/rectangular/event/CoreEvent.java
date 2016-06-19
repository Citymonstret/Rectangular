package com.intellectualsites.rectangular.event;

import com.intellectualsites.rectangular.CoreModule;
import lombok.Getter;

public abstract class CoreEvent<Module extends CoreModule> extends RectangularEvent {

    @Getter
    private final Module module;

    public CoreEvent(Module module) {
        this.module = module;
    }

}
