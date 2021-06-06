package me.astrash.discordmetabot.discord.listener;

import me.astrash.discordmetabot.command.CommandHolder;
import me.astrash.discordmetabot.command.EventCommand;
import me.astrash.discordmetabot.command.StringCommandArgs;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Objects;

public class SlashCommandListener extends ListenerAdapter {

    private final CommandHolder<EventCommand<SlashCommandEvent>> commandHolder;

    public SlashCommandListener(CommandHolder<EventCommand<SlashCommandEvent>> commandHolder) {
        this.commandHolder = commandHolder;
    }

    @Override
    public void onSlashCommand(SlashCommandEvent event) {
        if (event.getName().equals("w")) {
            String input = Objects.requireNonNull(event.getOption("query")).getAsString();
            commandHolder.fetch("w").run(new StringCommandArgs(input), event);
        }
    }
}
