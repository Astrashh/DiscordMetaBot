package me.astrash.discordmetabot.command.commands.tag;

import com.dfsek.tectonic.exception.ConfigException;
import me.astrash.discordmetabot.command.CommandArgs;
import me.astrash.discordmetabot.command.EventCommand;
import me.astrash.discordmetabot.index.TagIndex;
import me.astrash.discordmetabot.util.discord.MessageUtil;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.io.IOException;

public class ReloadTagsCommand implements EventCommand<MessageReceivedEvent> {

    private final TagIndex tagIndex;

    public ReloadTagsCommand(TagIndex tagIndex) {
        this.tagIndex = tagIndex;
    }

    @Override
    public void run(CommandArgs args, MessageReceivedEvent event) {
        System.out.println(args.getRaw());

        try {
            tagIndex.reloadTags();
            event.getChannel().sendMessage("Reloaded tags").queue();
        } catch (IOException | ConfigException e) {
            e.printStackTrace();
            MessageUtil.sendError("An error has occurred while reloading tags:", event.getChannel(), e, false);
        }
    }
}
