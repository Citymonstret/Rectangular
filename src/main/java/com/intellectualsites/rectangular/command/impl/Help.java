package com.intellectualsites.rectangular.command.impl;

import com.intellectualsites.commands.Command;
import com.intellectualsites.commands.CommandInstance;
import com.intellectualsites.commands.pagination.PaginatedCommand;
import com.intellectualsites.commands.pagination.PaginationFactory;
import com.intellectualsites.rectangular.command.RectangularCommandManager;
import com.intellectualsites.rectangular.config.Message;
import com.intellectualsites.rectangular.parser.impl.IntegerParser;

public class Help extends PaginatedCommand<Command> {

    public Help() {
        super(Command.class, () -> RectangularCommandManager.instance.getCommands(),
                5, "help", "help", "Show a list of all possible commands", "", new String[0], Object.class);

        withArgument("page", new IntegerParser(1, Integer.MAX_VALUE), "The help page");
    }

    @Override
    public boolean handleTooBigPage(CommandInstance instance, int specifiedPage, int maxPages) {
        Message.ERROR_PARSER_TOO_BIG.send(instance.getCaller(), specifiedPage, maxPages);
        return true;
    }

    @Override
    public boolean onCommand(PaginatedCommandInstance<Command> instance) {
        PaginationFactory<Command> factory = getPaginationFactory();
        Message.HELP_HEADER.send(instance.getCaller(), instance.getPage().getPageNum() + 1, factory.getPages().size());
        for (Command command : instance.getPage().getItems()) {
            Message.HELP_ENTRY.send(instance.getCaller(), command.getUsage(), command.getDescription());
        }
        Message.HELP_FOOTER.send(instance.getCaller());
        return true;
    }

}
