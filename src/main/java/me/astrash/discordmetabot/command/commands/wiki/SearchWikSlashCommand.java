package me.astrash.discordmetabot.command.commands.wiki;

import me.astrash.discordmetabot.command.CommandArgs;
import me.astrash.discordmetabot.command.EventCommand;
import me.astrash.discordmetabot.wiki.WikiSearcher;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent.OptionData;

import java.util.Objects;

public class SearchWikSlashCommand implements EventCommand<SlashCommandEvent> {

    private WikiSearcher searcher;

    public SearchWikSlashCommand(WikiSearcher searcher) {
        this.searcher = searcher;
    }

    @Override
    public void run(CommandArgs args, SlashCommandEvent event) {

        boolean isPublic = false;
        OptionData privateData = event.getOption("public");
        if (privateData != null) isPublic = privateData.getAsBoolean();

        int count = WikiSearcher.MAX_RESULTS_DEFAULT;
        OptionData countData = event.getOption("count");
        if (countData != null) count = Math.toIntExact(countData.getAsLong());

        count = isPublic ? Math.min(count, WikiSearcher.MAX_RESULTS_PUBLIC) : Math.min(count, WikiSearcher.MAX_RESULTS_PRIVATE);

        String query = Objects.requireNonNull(event.getOption("query")).getAsString();
        event.reply(searcher.search(query, count)).setEphemeral(!isPublic).queue();
    }
}
