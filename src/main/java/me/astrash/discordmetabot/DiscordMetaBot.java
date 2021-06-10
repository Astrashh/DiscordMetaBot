package me.astrash.discordmetabot;

import com.dfsek.tectonic.exception.ConfigException;
import me.astrash.discordmetabot.command.CommandHolder;
import me.astrash.discordmetabot.command.EventCommand;
import me.astrash.discordmetabot.command.commands.tag.DisplayTagCommand;
import me.astrash.discordmetabot.command.commands.tag.ListTagsSlashCommand;
import me.astrash.discordmetabot.command.commands.wiki.SearchWikSlashCommand;
import me.astrash.discordmetabot.config.ConfigHandler;
import me.astrash.discordmetabot.discord.listener.MessageReceivedListener;
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
        WikiSearcher wikiSearcher = new WikiSearcher(wikiIndex, configHandler);

        // Create an index for tags
        logger.info("Loading tag files...");
        TagIndex tagIndex = new TagIndex(tagPath);

        JDABuilder botBuilder = JDABuilder.createDefault(configHandler.getConfig().getDiscordBotToken());

        // Registering event handlers + event commands
        CommandHolder<EventCommand<MessageReceivedEvent>> receivedEventCommandHolder = new CommandHolder<EventCommand<MessageReceivedEvent>>()
                .registerCommand(new DisplayTagCommand(tagIndex), "tag", "t", "info", "i");
                //.registerCommand(new SearchWikiMessageCommand(wikiSearcher), "wiki", "w")
                //.registerCommand(new ReloadTagsCommand(tagIndex), "reload", "r")
                //.registerCommand(new ReadTagFileCommand(tagIndex), "read", "cat")
                //.registerCommand(new ListTagFilesCommand(tagIndex), "list", "ls")
                //.registerCommand(new ListTagsCommand(tagIndex), "tags", "ts")
                //.registerCommand(new EditTagFileCommand(tagEdits, tagIndex), "edit");
        botBuilder.addEventListeners(new MessageReceivedListener(configHandler, receivedEventCommandHolder));

        CommandHolder<EventCommand<SlashCommandEvent>> slashCommandHolder = new CommandHolder<EventCommand<SlashCommandEvent>>()
                .registerCommand(new SearchWikSlashCommand(wikiSearcher), "w")
                .registerCommand(new ListTagsSlashCommand(tagIndex), "tags");
        botBuilder.addEventListeners(new SlashCommandListener(slashCommandHolder));

        //Map<String, String> tagEdits = new HashMap<>();
        //CommandHolder<EventCommand<MessageUpdateEvent>> editCommandHolder = new CommandHolder<EventCommand<MessageUpdateEvent>>()
        //        .registerCommand(new EditTagUpdateCommand(tagEdits, tagIndex), "editupdate");
        //botBuilder.addEventListeners(new MessageUpdateListener(editCommandHolder));

        CommandData wikiCommandData = new CommandData("w", "Searches the Terra wiki.")
                .addOption(new OptionData(OptionType.STRING, "query", "The page you're looking for. Includes both page titles, and subheadings.").setRequired(true))
                .addOption(new OptionData(OptionType.BOOLEAN, "public", "Whether others can see your query."))
                .addOption(new OptionData(OptionType.INTEGER, "count", "How many pages to return."));
        CommandData listTagsCommandData = new CommandData("tags", "Lists available tags.");

        logger.info("Connecting to Discord via JDA...");
        try {
            JDA bot = botBuilder.build().awaitReady();

            // For now, register slash commands in testing guild
            Guild guild = bot.getGuildsByName(configHandler.getConfig().getTestGuildName(), true).get(0);
            if (guild != null) {
                guild.updateCommands().addCommands(
                        wikiCommandData,
                        listTagsCommandData
                ).queue();
            }
        } catch (LoginException | InterruptedException e) { logger.error("Failed to set up Discord bot!", e); }
    }

    public static void main(String[] args) throws IOException, ConfigException {
        new DiscordMetaBot();
    }
}