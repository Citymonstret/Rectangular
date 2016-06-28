package com.intellectualsites.rectangular.config;

import com.intellectualsites.commands.callers.CommandCaller;
import com.intellectualsites.rectangular.Rectangular;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Message {
    ERROR_IN_REGION("&cYou're already in a region"),

    ERROR_BOUNDS_OVERLAPPING("&c&lCannot create region: &6The bounds are overlapping with region(s): &e{0}"),
    ERROR_BOUNDS_NOT_SHARING_EDGE("&c&lCannot expand region: &6The rectangle does not share any edges with the region"),
    ERROR_BOUNDS_INSIDE_REGION("&c&lCannot expand region: &6The rectangle is inside of the region"),

    ERROR_HAS_TOOL("&cYou already have the selection tool"),

    ERROR_MISSING_SELECTION("&cYou need a complete selection in order to use that command"),

    ERROR_PARSER_TOO_BIG("&6{0} &cis too big, max: &6{1}"),

    INFO_CREATED_REGION("&6Created region with ID: &e{0}"),
    INFO_ADDING_TO_MANAGER("&6Attempting to register the region"),
    INFO_SUCCESS("&6&lSuccess!"),

    HELP_HEADER("&c&l// &6Page: &e{0}&c/&e{1}"),
    HELP_FOOTER("null"),
    HELP_ENTRY("&c&l/ &e/rect {0} &c| &6Desc: &e{1}"),

    TIP_USE_EXPAND("&6Use &e/rectangular expand"),
    TIP_USE_WAND("&6Use &e/rectangular wand &6to get a selection wand")
    ;

    @Getter
    private final String defaultString;

    @Override
    public String toString() {
        return name().toLowerCase().replace("_", ".");
    }

    public String getIdentifier() {
        return "@" + this.toString();
    }

    public void send(CommandCaller caller, Object ... args) {
        caller.message(Rectangular.get().getMessageManager().getMessage(toString()), args);
    }
}
