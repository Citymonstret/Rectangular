package com.intellectualsites.rectangular.parser.impl;

import com.intellectualsites.rectangular.parser.Parser;

public class StringParser extends Parser<String> {

    public StringParser() {
        super("string", "Example...");
    }

    @Override
    public String parse(String in) {
        return in;
    }

}
