package ru.exlmoto.digestbot.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.exlmoto.digestbot.annotations.ChatCommand;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class CommandsManager {
	private Map<String, Command> mCommands;

	@Autowired
	public void setCommands(List<Command> aCommandList) {
		this.mCommands = aCommandList.stream()
		        .filter(command -> command.getClass().isAnnotationPresent(ChatCommand.class))
		        .collect(Collectors.toMap(command -> command.getClass()
		                .getAnnotation(ChatCommand.class).name(), Function.identity()));

		mCommands.forEach((k, v) ->
		        System.out.println(String.format("Command %s has been registered with class %s", k, v.getClass().getSimpleName())));
	}

	private Command resolve(String aName) {
		return this.mCommands.containsKey(aName) ? this.mCommands.get(aName) : aCommandsContext -> { };
	}

	public void executeCommand(Command aCommand) {
		aCommand.execute();
	}

	public void executeCommand(Command aCommand, CommandsContext aCommandsContext) {
		aCommand.execute(aCommandsContext);
	}

	public void executeCommand(String aCommandName) {
		executeCommand(resolve(aCommandName));
	}

	public void executeCommand(String aCommandName, CommandsContext aCommandsContext) {
		executeCommand(resolve(aCommandName), aCommandsContext);
	}
}
