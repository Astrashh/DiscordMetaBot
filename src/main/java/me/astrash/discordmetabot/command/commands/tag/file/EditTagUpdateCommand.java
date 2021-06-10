package me.astrash.discordmetabot.command.commands.tag.file;

import com.dfsek.tectonic.exception.ConfigException;
import me.astrash.discordmetabot.command.CommandArgs;
import me.astrash.discordmetabot.command.EventCommand;
import me.astrash.discordmetabot.index.TagIndex;
import me.astrash.discordmetabot.util.discord.MessageUtil;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.utils.MarkdownUtil;

import java.util.Map;

public class EditTagUpdateCommand implements EventCommand<MessageUpdateEvent> {

    private final Map<String, String> tagEdits;
    private final TagIndex tagIndex;

    public EditTagUpdateCommand(Map<String, String> tagEdits, TagIndex tagIndex) {
        this.tagEdits = tagEdits;
        this.tagIndex = tagIndex;
    }

    @Override
    public void run(CommandArgs args, MessageUpdateEvent event) {
        if (tagEdits.containsKey(event.getMessageId())) {
            String previewMessageId = tagEdits.get(event.getMessageId());
            String codeBlock = MessageUtil.getFirstCodeBlock(event.getMessage().getContentRaw());
            try {
                MessageEmbed embed = tagIndex.testEmbed(codeBlock, "test");
                if (embed != null && embed.isSendable()) {
                    event.getChannel().editMessageById(previewMessageId, embed).content("").queue();
                } else {
                    event.getChannel().editMessageById(previewMessageId, MarkdownUtil.codeblock("diff", "- Invalid tag config!")).queue();
                }
            } catch (ConfigException e) {
                event.getChannel().editMessageById(previewMessageId, MarkdownUtil.codeblock("diff", "- " + e.toString())).queue();
            }
        }
    }
}
