package com.intellectualsites.rectangular.parser.impl;

import com.intellectualsites.rectangular.parser.InstantArray;
import com.intellectualsites.rectangular.parser.Parser;

public class StringArrayParser extends Parser<String> implements InstantArray {

    public StringArrayParser() {
        super("strings", "Multiple words...");
    }

    @Override
    public String parse(String in) {
        return in;
    }
}
