package me.astrash.discordmetabot.command.commands.tag;

import me.astrash.discordmetabot.command.CommandArgs;
import me.astrash.discordmetabot.command.EventCommand;
import me.astrash.discordmetabot.index.TagIndex;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class DisplayTagCommand implements EventCommand<MessageReceivedEvent> {

    private final TagIndex tagIndex;

    public DisplayTagCommand(TagIndex tagIndex) {
        this.tagIndex = tagIndex;
    }

    @Override
    public void run(CommandArgs args, MessageReceivedEvent event) {
        MessageEmbed msg = tagIndex.query(args.getRaw());
        if (msg != null) {
            event.getChannel().sendMessage(msg).queue();
        }
    }
}
