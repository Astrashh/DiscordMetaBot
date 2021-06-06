package me.astrash.discordmetabot.command.commands.tag;

import me.astrash.discordmetabot.command.CommandArgs;
import me.astrash.discordmetabot.command.EventCommand;
import me.astrash.discordmetabot.index.DiscreteIndex;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.utils.MarkdownUtil;

public class ListTagsCommand implements EventCommand<MessageReceivedEvent> {

    private final DiscreteIndex<String, MessageEmbed> tagIndex;

    public ListTagsCommand(DiscreteIndex<String, MessageEmbed> tagIndex) {
        this.tagIndex = tagIndex;
    }

    @Override
    public void run(CommandArgs args, MessageReceivedEvent event) {
        StringBuilder builder = new StringBuilder();
        tagIndex.getAll().keySet().forEach(key -> builder.append("\u2022 ").append(key).append("\n"));
        event.getChannel().sendMessage("Availble tags:\n" + MarkdownUtil.codeblock(builder.toString())).queue();
    }
}
