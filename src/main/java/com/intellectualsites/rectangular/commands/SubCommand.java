package com.intellectualsites.rectangular.commands;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Collection;

@RequiredArgsConstructor
public enum SubCommand {
    INFO("info", Arrays.asList("i", "about")),
    SETUP("setup", Arrays.asList("create", "s"))
    ;

    @Getter
    private final String command;

    @Getter
    private final Collection<String> aliases;

    @Override
    public String toString() {
        return getCommand();
    }
}
