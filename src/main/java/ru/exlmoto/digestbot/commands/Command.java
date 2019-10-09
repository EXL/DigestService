package ru.exlmoto.digestbot.commands;

@FunctionalInterface
public interface Command {
	default void execute() {
		execute(new CommandsContext());
	}
	void execute(CommandsContext aCommandsContext);
}
