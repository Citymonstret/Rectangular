package com.intellectualsites.rectangular.parser;

import lombok.Getter;

public abstract class Parser<T> {

    @Getter
    private String name;

    @Getter
    private final T example;

    public Parser(String name, T example) {
        this.name = name;
        this.example = example;
    }

    public abstract ParserResult<T> parse(String in);

    @Override
    public final String toString() {
        return this.getName();
    }
}
