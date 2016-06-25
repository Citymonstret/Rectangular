package com.intellectualsites.commands;

import com.intellectualsites.commands.callers.CommandCaller;
import com.intellectualsites.commands.util.StringComparison;
import lombok.Getter;
import lombok.Setter;

public class CommandResult {

    private final Command command;
    private final CommandManager manager;
    private final CommandCaller caller;
    private Command closestMatch;
    private Throwable throwable;
    private final int commandResult;

    @Getter
    private String input;

    @Getter
    private CommandArgumentError commandArgumentError;

    protected CommandResult(String input, CommandManager commandManager, Command command, CommandCaller caller, int commandResult, Throwable throwable, CommandArgumentError error) {
        this.command = command;
        this.caller = caller;
        this.manager = commandManager;
        this.commandResult = commandResult;
        this.input = input;
        this.commandArgumentError = error;

        if (commandManager.getManagerOptions().getFindCloseMatches()) {
            if (commandResult == CommandHandlingOutput.NOT_FOUND) {
                closestMatch = new StringComparison<Command>(input, commandManager.getCommands().toArray(new Command[0])).getMatchObject();
            }
        }

        if (closestMatch == null) {
            if (command != null) {
                closestMatch = command;
            }
        }

        if (throwable != null) {
            this.throwable = throwable;
        }

        if (throwable != null && manager.getManagerOptions().getPrintStacktrace()) {
            throwable.printStackTrace();
        }
    }

    public Throwable getStacktrace() {
        return this.throwable;
    }

    public int getCommandResult() {
        return this.commandResult;
    }

    public CommandCaller getCaller() {
        return this.caller;
    }

    public CommandManager getManager() {
        return this.manager;
    }

    public Command getCommand() {
        return this.command;
    }

    public Command getClosestMatch() {
        return this.closestMatch;
    }

    protected static class CommandResultBuilder {

        private int commandResult = CommandHandlingOutput.OUTPUT_BUILD_FAILURE;
        private CommandCaller caller = null;
        private CommandManager manager = null;
        private Command command = null;
        private String input = "";
        private Throwable throwable = null;
        @Setter
        private CommandArgumentError commandArgumentError = null;

        protected CommandResultBuilder(){}

        public void setStacktrace(Throwable throwable) {
            this.throwable = throwable;
        }

        public void setCommandResult(int commandResult) {
            this.commandResult = commandResult;
        }

        public void setCommand(Command command) {
            this.command = command;
        }

        public void  setCaller(CommandCaller caller) {
            this.caller = caller;
        }

        public void setManager(CommandManager manager) {
            this.manager = manager;
        }

        public void setInput(String string) {
            this.input = string;
        }

        public CommandResult build() {
            return new CommandResult(input, manager, command, caller, commandResult, throwable, commandArgumentError);
        }
    }
}
