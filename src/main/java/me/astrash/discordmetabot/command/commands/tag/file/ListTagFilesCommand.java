package me.astrash.discordmetabot.command.commands.tag.file;

import me.astrash.discordmetabot.command.CommandArgs;
import me.astrash.discordmetabot.command.EventCommand;
import me.astrash.discordmetabot.index.TagIndex;
import me.astrash.discordmetabot.util.FileUtil;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.utils.MarkdownUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class ListTagFilesCommand implements EventCommand<MessageReceivedEvent> {

    private final TagIndex tagIndex;

    public ListTagFilesCommand(TagIndex tagIndex) {
        this.tagIndex = tagIndex;
    }

    @Override
    public void run(CommandArgs args, MessageReceivedEvent event) {
        try {
            Path searchPath = tagIndex.getTagPath();
            List<Path> endPaths = Files.walk(searchPath)
                    .filter(Files::isRegularFile)
                    .map(searchPath::relativize)
                    .collect(Collectors.toList());

            StringBuilder builder = new StringBuilder();
            endPaths.forEach(path -> builder.append("\u2022 ").append(FileUtil.removeExtension(path.toString())).append("\n"));
            event.getChannel().sendMessage("Available tag files:\n" + MarkdownUtil.codeblock(builder.toString())).queue();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
