package com.intellectualsites.rectangular.core;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class ContainerFactory<Container extends RegionContainer> {

    private final char prefix;

    public char getRawPrefix() {
        return this.prefix;
    }

    public String getPrefix() {
        return this.prefix + ":";
    }

    public abstract Container getContainer(String key);

    public abstract boolean hasContainer(String key);

}
