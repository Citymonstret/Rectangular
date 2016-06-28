package com.intellectualsites.commands.callers;

import com.google.common.collect.ImmutableCollection;
import com.intellectualsites.commands.Command;
import com.intellectualsites.commands.CommandManager;
import com.intellectualsites.rectangular.parser.Parserable;

public interface CommandCaller<T> {

    void message(String message, Object ... arguments);

    T getSuperCaller();

    boolean hasAttachment(String a);

    void sendRequiredArgumentsList(CommandManager manager, Command cmd, ImmutableCollection<Parserable> required, String usage);
}
