package me.astrash.discordmetabot;

import com.dfsek.tectonic.exception.ConfigException;
import me.astrash.discordmetabot.command.EventCommand;
import me.astrash.discordmetabot.command.CommandHolder;
import me.astrash.discordmetabot.command.commands.SearchWikiCommand;
import me.astrash.discordmetabot.command.commands.tag.DisplayTagCommand;
import me.astrash.discordmetabot.command.commands.tag.ListTagsCommand;
import me.astrash.discordmetabot.command.commands.tag.ReloadTagsCommand;
import me.astrash.discordmetabot.command.commands.tag.file.EditTagFileCommand;
import me.astrash.discordmetabot.command.commands.tag.file.ListTagFilesCommand;
import me.astrash.discordmetabot.command.commands.tag.file.ReadTagFileCommand;
import me.astrash.discordmetabot.config.ConfigHandler;
import me.astrash.discordmetabot.discord.listener.MessageCommandListener;
import me.astrash.discordmetabot.discord.listener.SlashCommandListener;
import me.astrash.discordmetabot.index.Index;
import me.astrash.discordmetabot.index.TagIndex;
import me.astrash.discordmetabot.index.page.PageResult;
import me.astrash.discordmetabot.index.page.lucene.LuceneIndex;
import me.astrash.discordmetabot.util.git.GitUtil;
import me.astrash.discordmetabot.wiki.WikiSearcher;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Command.OptionType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.requests.restaction.CommandUpdateAction.CommandData;
import net.dv8tion.jda.api.requests.restaction.CommandUpdateAction.OptionData;
import org.eclipse.jgit.api.errors.GitAPIException;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class DiscordMetaBot {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(DiscordMetaBot.class);

    private final ConfigHandler configHandler;

    public DiscordMetaBot() throws IOException, ConfigException {

        String resourceDir = "./resources";
        Path
            wikiRepoPath = Paths.get(resourceDir + "/wiki"),
            indexPath    = Paths.get(resourceDir + "/index"),
            tagPath      = Paths.get(resourceDir + "/tags");

        // Load config
        configHandler = new ConfigHandler(Paths.get(resourceDir + "/config.yml"));

        // Clone wiki repo locally
        if (configHandler.getConfig().cloneWiki()) {
            try {
                GitUtil.setupRepo(configHandler.getConfig().getWikiURI(), wikiRepoPath, configHandler.getConfig().getPullBranch());
            } catch (GitAPIException e) { logger.error("Failed to set up wiki repository!", e); }
        }

        // Create an index for wiki pages
        logger.info("Indexing repository...");
        Index<String, PageResult[]> wikiIndex = new LuceneIndex(wikiRepoPath, indexPath);

        // Create an index for tags
        logger.info("Loading tag files...");
        TagIndex tagIndex = new TagIndex(tagPath);

        Map<String, String> tagEdits = new HashMap<>();

        WikiSearcher wikiSearcher = new WikiSearcher(wikiIndex, configHandler);

        CommandHolder<EventCommand<MessageReceivedEvent>> receivedEventCommandHolder = new CommandHolder<EventCommand<MessageReceivedEvent>>()
                .registerCommand(new SearchWikiCommand(wikiSearcher), "wiki", "w")
                .registerCommand(new ReloadTagsCommand(tagIndex), "reload", "r")
                .registerCommand(new ReadTagFileCommand(tagIndex), "read", "cat")
                .registerCommand(new ListTagFilesCommand(tagIndex), "list", "ls")
                .registerCommand(new ListTagsCommand(tagIndex), "tags", "ts")
                .registerCommand(new DisplayTagCommand(tagIndex), "tag", "t")
                .registerCommand(new EditTagFileCommand(tagEdits, tagIndex), "edit");

        CommandHolder<EventCommand<SlashCommandEvent>> slashCommandHolder = new CommandHolder<EventCommand<SlashCommandEvent>>();

        try {
            JDABuilder botBuilder = JDABuilder.createDefault(configHandler.getConfig().getDiscordBotToken());
            JDA bot = botBuilder.addEventListeners(
                    new MessageCommandListener(configHandler, receivedEventCommandHolder),
                    new SlashCommandListener(slashCommandHolder)
                ).build().awaitReady();

            Guild guild = bot.getGuildsByName(configHandler.getConfig().getTestGuildName(), true).get(0);
            if (guild != null) {
                CommandData wikiCommand = new CommandData("w", "Searches the Terra wiki")
                        .addOption(new OptionData(OptionType.STRING, "query", "The page you're looking for.").setRequired(true))
                        .addOption(new OptionData(OptionType.INTEGER, "count", "How many pages to return."))
                        .addOption(new OptionData(OptionType.BOOLEAN, "private", "Whether only you can see your query."));
                guild.updateCommands().addCommands(wikiCommand).queue();
            }
        } catch (LoginException | InterruptedException e) { logger.error("Failed to set up Discord bot!", e); }
    }

    public static void main(String[] args) throws IOException, ConfigException {
        new DiscordMetaBot();
    }
}