package me.astrash.discordmetabot.command.commands.tag.file;

import com.dfsek.tectonic.exception.ConfigException;
import me.astrash.discordmetabot.command.CommandArgs;
import me.astrash.discordmetabot.command.EventCommand;
import me.astrash.discordmetabot.index.TagIndex;
import me.astrash.discordmetabot.util.discord.MessageUtil;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Map;

public class EditTagFileCommand implements EventCommand<MessageReceivedEvent> {

    private final Map<String, String> tagEdits;
    private final TagIndex tagIndex;

    public EditTagFileCommand(Map<String, String> tagEdits, TagIndex tagIndex) {
        this.tagEdits = tagEdits;
        this.tagIndex = tagIndex;
    }

    @Override
    public void run(CommandArgs args, MessageReceivedEvent event) {
        System.out.println("edit command ran: " + args);
        String codeBlock = MessageUtil.getFirstCodeBlock(args.getRaw());
        if (codeBlock != null) {
            System.out.println(codeBlock);
            try {
                MessageEmbed embed = tagIndex.testEmbed(codeBlock, "test");
                try {
                    event.getChannel().sendMessage(embed).queue(
                            // Keep track of edit message and preview message
                            message -> tagEdits.put(event.getMessageId(), message.getId())
                    );
                } catch (IllegalArgumentException e) { MessageUtil.sendError("Could not display tag:", event.getChannel(), e, false); }
            } catch (ConfigException e) { MessageUtil.sendError("Could not display tag:", event.getChannel(), e, false); }
        } else {
            MessageUtil.sendError("You need to provide a code block.", event.getChannel());
        }
    }
}
