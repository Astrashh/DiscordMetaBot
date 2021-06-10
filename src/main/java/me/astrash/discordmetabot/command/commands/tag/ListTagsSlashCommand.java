package me.astrash.discordmetabot.command.commands.tag;

import me.astrash.discordmetabot.command.CommandArgs;
import me.astrash.discordmetabot.command.EventCommand;
import me.astrash.discordmetabot.index.DiscreteIndex;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.utils.MarkdownUtil;

public class ListTagsSlashCommand implements EventCommand<SlashCommandEvent> {

    private final DiscreteIndex<String, MessageEmbed> tagIndex;

    public ListTagsSlashCommand(DiscreteIndex<String, MessageEmbed> tagIndex) {
        this.tagIndex = tagIndex;
    }

    @Override
    public void run(CommandArgs args, SlashCommandEvent event) {
        if (tagIndex.getAll().size() == 0) {
            event.reply("No tags available").setEphemeral(true).queue();
            return;
        }
        StringBuilder builder = new StringBuilder();
        tagIndex.getAll().keySet().forEach(key -> builder.append("\u2022 ").append(key).append("\n"));
        event.reply("Availble tags:\n" + MarkdownUtil.codeblock(builder.toString())).setEphemeral(true).queue();
    }
}
