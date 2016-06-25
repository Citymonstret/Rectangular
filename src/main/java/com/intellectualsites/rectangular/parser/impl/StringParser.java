package com.intellectualsites.rectangular.parser.impl;

import com.intellectualsites.rectangular.parser.Parser;
import com.intellectualsites.rectangular.parser.ParserResult;

public class StringParser extends Parser<String> {

    public StringParser() {
        super("string", "Example...");
    }

    @Override
    public ParserResult<String> parse(String in) {
        return new ParserResult<>(in, true);
    }

}
