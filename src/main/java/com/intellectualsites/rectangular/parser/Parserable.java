package com.intellectualsites.rectangular.parser;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Parserable<T> {

    @Getter
    private final String name;

    @Getter
    private final Parser<T> parser;

    @Getter
    private final String desc;

    public T parse(String in) {
        return parser.parse(in);
    }
}
