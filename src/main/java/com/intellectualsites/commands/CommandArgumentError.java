package com.intellectualsites.commands;

import com.intellectualsites.rectangular.parser.ParserResult;
import com.intellectualsites.rectangular.parser.Parserable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CommandArgumentError {

    @Getter
    private final ParserResult result;

    @Getter
    private final Parserable parserable;

}
