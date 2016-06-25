package com.intellectualsites.rectangular.command.impl;

import com.intellectualsites.commands.Command;
import com.intellectualsites.commands.CommandInstance;
import com.intellectualsites.commands.pagination.PaginatedCommand;
import com.intellectualsites.commands.pagination.PaginationFactory;
import com.intellectualsites.rectangular.command.RectangularCommandManager;
import com.intellectualsites.rectangular.parser.impl.IntegerParser;

import java.util.ArrayList;
import java.util.List;

public class Help extends PaginatedCommand<Command> {

    public Help() {
        super(Command.class, () -> RectangularCommandManager.instance.getCommands(),
                5, "help", "help", "Show a list of all possible commands", "", new String[0], Object.class);

        withArgument("page", new IntegerParser(1, Integer.MAX_VALUE), "The help page");
    }

    @Override
    public boolean handleTooBigPage(CommandInstance instance, int specifiedPage, int maxPages) {
        instance.getCaller().message(String.format("&6%d &cis too big, max: &6%d", specifiedPage, maxPages));
        return true;
    }

    @Override
    public boolean onCommand(PaginatedCommandInstance<Command> instance) {
        PaginationFactory<Command> factory = getPaginationFactory();

        List<String> list = new ArrayList<>();
        list.add("&c&l// &6Page: &e" + (instance.getPage().getPageNum() + 1) + "&c/&e" + factory.getPages().size());

        for (Command command : instance.getPage().getItems()) {
            list.add(String.format("&c&l/ &e/rect %s &c| &6Desc: &e%s", command.getUsage(), command.getDescription()));
        }

        list.forEach(instance.getCaller()::message);

        return true;
    }

}
