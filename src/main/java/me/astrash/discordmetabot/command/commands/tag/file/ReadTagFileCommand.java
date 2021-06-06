package me.astrash.discordmetabot.command.commands.tag.file;

import me.astrash.discordmetabot.command.CommandArgs;
import me.astrash.discordmetabot.command.EventCommand;
import me.astrash.discordmetabot.index.TagIndex;
import me.astrash.discordmetabot.util.FileUtil;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.utils.MarkdownUtil;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ReadTagFileCommand implements EventCommand<MessageReceivedEvent> {

    private final static String invalidDirectory = MarkdownUtil.codeblock("diff", "- That directory is invalid");

    private final TagIndex tagIndex;

    public ReadTagFileCommand(TagIndex tagIndex) {
        this.tagIndex = tagIndex;
    }

    @Override
    public void run(CommandArgs args, MessageReceivedEvent event) {
        Path tagPath = tagIndex.getTagPath().normalize();
        Path inputPath = Paths.get(args.getRaw() + ".yml");
        if (FileUtil.isSafePath(tagIndex.getTagPath(), inputPath)) {
            try {
                event.getChannel().sendMessage(MarkdownUtil.codeblock("yaml", FileUtil.readFile(tagPath.resolve(inputPath)))).queue();
            } catch (IOException e) {
                event.getChannel().sendMessage(invalidDirectory).queue();
            }
        } else {
            event.getChannel().sendMessage(invalidDirectory).queue();
        }
    }
}
