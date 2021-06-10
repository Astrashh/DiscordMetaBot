package me.astrash.discordmetabot.command.commands.wiki;

import me.astrash.discordmetabot.command.CommandArgs;
import me.astrash.discordmetabot.command.EventCommand;
import me.astrash.discordmetabot.wiki.WikiSearcher;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class SearchWikiMessageCommand implements EventCommand<MessageReceivedEvent> {

    private WikiSearcher searcher;

    public SearchWikiMessageCommand(WikiSearcher searcher) {
        this.searcher = searcher;
    }

    @Override
    public void run(CommandArgs args, MessageReceivedEvent event) {
        event.getChannel().sendMessage(searcher.search(args.getRaw())).queue();
    }
}
