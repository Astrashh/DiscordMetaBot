package me.astrash.discordmetabot.command;

import java.util.HashMap;
import java.util.Map;

public class CommandHolder<T extends EventCommand<?>> {

    private Map<String, T> commands = new HashMap<>();

    public CommandHolder<T> registerCommand(T command, String alias) {
        commands.put(alias, command);
        return this;
    }

    public CommandHolder<T> registerCommand(T command, String... aliases) {
        for (String alias : aliases) {
            registerCommand(command, alias);
        }
        return this;
    }

    public T fetch(String alias) {
        T command = commands.get(alias);
        if (command != null) {
            return command;
        } else {
            return (T) EventCommand.NULL_COMMAND;
        }
    }
}