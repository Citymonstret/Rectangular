package com.intellectualsites.rectangular.parser.impl;

import com.intellectualsites.rectangular.parser.Parser;

public class IntegerParser extends Parser<Integer> {

    public IntegerParser() {
        super("int", 1337);
    }

    @Override
    public Integer parse(String in) {
        Integer value = null;
        try {
            value = java.lang.Integer.parseInt(in);
        } catch(final Exception ignored) {
            ignored.printStackTrace();
        }
        return value;
    }

}
