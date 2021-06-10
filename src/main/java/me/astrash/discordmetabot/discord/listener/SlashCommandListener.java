package me.astrash.discordmetabot.discord.listener;

import me.astrash.discordmetabot.command.*;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class SlashCommandListener extends ListenerAdapter {

    private final CommandHolder<EventCommand<SlashCommandEvent>> commandHolder;

    public SlashCommandListener(CommandHolder<EventCommand<SlashCommandEvent>> commandHolder) {
        this.commandHolder = commandHolder;
    }

    @Override
    public void onSlashCommand(SlashCommandEvent event) {
        EventCommand<SlashCommandEvent> command = commandHolder.fetch(event.getName());
        if (command != null) command.run(CommandArgs.EMPTY, event); // Let command fetch arguments from event
    }
}