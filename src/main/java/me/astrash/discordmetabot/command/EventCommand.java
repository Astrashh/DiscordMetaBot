package me.astrash.discordmetabot.command;

import net.dv8tion.jda.api.events.Event;

public interface EventCommand<T extends Event> {
    void run(CommandArgs args, T event);
}
