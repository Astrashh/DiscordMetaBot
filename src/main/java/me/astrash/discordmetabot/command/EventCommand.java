package me.astrash.discordmetabot.command;

import net.dv8tion.jda.api.events.Event;

public interface EventCommand<T extends Event> {

    EventCommand<Event> NULL_COMMAND = (rawArgs, event) -> {};

    void run(CommandArgs args, T event);
}
