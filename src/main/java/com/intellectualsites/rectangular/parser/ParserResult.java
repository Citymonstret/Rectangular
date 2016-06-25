package com.intellectualsites.rectangular.parser;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ParserResult<T> {

    @Getter
    private final T result;

    @Getter
    private final boolean parsed;

    @Getter
    private String error;

    public ParserResult(T result) {
        this.result = result;
        this.parsed = true;
    }

    public ParserResult(String error) {
        this.result = null;
        this.parsed = false;
        this.error = error;
    }

}
