package com.intellectualsites.rectangular.parser.impl;

import com.intellectualsites.rectangular.parser.Parser;
import com.intellectualsites.rectangular.parser.ParserResult;

public class IntegerParser extends Parser<Integer> {

    private int min, max;
    private boolean hasRange = false;

    public IntegerParser() {
        super("int", 1337);
    }

    public IntegerParser(int min, int max) {
        this();
        this.min = min;
        this.max = max;
        this.hasRange = true;
    }

    @Override
    public ParserResult<Integer> parse(String in) {
        Integer value = null;
        try {
            value = java.lang.Integer.parseInt(in);
        } catch(final Exception ignored) {
        }
        if (hasRange && value != null) {
            int i = value;
            i = Math.min(i, max);
            i = Math.max(i, min);
            if (i != value) {
                return new ParserResult<>(value +
                        " is not within range (" + min + " -> " +
                        (max == Integer.MAX_VALUE ? "infinity" : max)
                        + ")");
            }
        }
        if (value != null) {
            return new ParserResult<>(value);
        }
        return new ParserResult<>(in + " is not an integer");
    }

}
