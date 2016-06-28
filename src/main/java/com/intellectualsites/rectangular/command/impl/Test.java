package com.intellectualsites.rectangular.command.impl;

import com.intellectualsites.commands.Command;
import com.intellectualsites.commands.CommandDeclaration;
import com.intellectualsites.commands.CommandInstance;
import com.intellectualsites.rectangular.parser.impl.StringParser;

@CommandDeclaration(
        command = "test",
        aliases = { "t" }
)
public class Test extends Command {

    public Test() {
        withArgument("message", new StringParser(), "A message that will be echoed back to you");
    }

    @Override
    public boolean onCommand(CommandInstance instance) {
        instance.getCaller().message("&cThe message was &6{0}", instance.getString("message"));
        return true;
    }


}
