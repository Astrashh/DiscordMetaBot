package me.astrash.discordmetabot.command;

import java.util.HashMap;
import java.util.Map;

public class CommandHolder<T extends EventCommand<?>> {

    private final Map<String, T> commands = new HashMap<>();

    public CommandHolder<T> registerCommand(T command, String name, String... aliases) {
        commands.put(name, command);
        for (String alias : aliases) {
            registerCommand(command, alias);
        }
        return this;
    }

    public T fetch(String alias) {
        return commands.get(alias);
    }
}