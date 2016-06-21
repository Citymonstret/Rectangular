package com.intellectualsites.rectangular.parser.impl;

import com.google.common.collect.ImmutableList;
import com.intellectualsites.rectangular.parser.Parser;

public class BooleanParser extends Parser<Boolean> {

    private static final ImmutableList<String> trueValues;
    private static final ImmutableList<String> falseValues;

    static {
        trueValues = new ImmutableList.Builder<String>()
                .add("yes").add("true").add("on").add("1").build();
        falseValues = new ImmutableList.Builder<String>()
                .add("no").add("false").add("off").add("0").build();
    }

    public BooleanParser() {
        super("boolean", true);
    }

    @Override
    public Boolean parse(String in) {
        if (trueValues.contains(in.toLowerCase())) {
            return true;
        } else if (falseValues.contains(in.toLowerCase())) {
            return false;
        }
        return null;
    }
}
