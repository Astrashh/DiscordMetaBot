package me.astrash.discordmetabot;

import me.astrash.discordmetabot.config.ConfigHandler;
import me.astrash.discordmetabot.discord.TagCommandListener;
import me.astrash.discordmetabot.discord.WikiCommandListener;
import me.astrash.discordmetabot.index.TagIndex;
import me.astrash.discordmetabot.index.page.PageIndex;
import me.astrash.discordmetabot.index.page.lucene.LuceneIndexer;
import me.astrash.discordmetabot.util.git.GitUtil;
import net.dv8tion.jda.api.JDABuilder;
import org.eclipse.jgit.api.errors.GitAPIException;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DiscordMetaBot {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(DiscordMetaBot.class);

    private final ConfigHandler configHandler;

    public DiscordMetaBot() throws IOException {

        String resourceDir = "./resources";
        Path
            wikiRepoPath = Paths.get(resourceDir + "/wiki"),
            indexPath    = Paths.get(resourceDir + "/index"),
            tagPath      = Paths.get(resourceDir + "/tags");

        // Load config
        configHandler = new ConfigHandler(Paths.get(resourceDir + "/config.yml"));

        // Clone wiki repo locally
        try {
            GitUtil.setupRepo(configHandler.getConfig().getWikiURI(), wikiRepoPath, configHandler.getConfig().getPullBranch());
        } catch (GitAPIException e) { logger.error("Failed to set up wiki repository!", e); }

        // Create an index for wiki pages
        logger.info("Indexing repository...");
        PageIndex wikiIndex = new LuceneIndexer(wikiRepoPath, indexPath);

        // Create an index for tags
        logger.info("Loading tag files...");
        TagIndex tagIndex = new TagIndex(tagPath);

        // Set up bot
        try {
            JDABuilder builder = JDABuilder.createDefault(configHandler.getConfig().getDiscordBotToken());
            builder
                .addEventListeners(
                    new TagCommandListener(tagIndex),
                    new WikiCommandListener(wikiIndex))
                .build();
        } catch (LoginException e) { logger.error("Failed to set up Discord bot!", e); }
    }

    public static void main(String[] args) throws IOException {
        new DiscordMetaBot();
    }
}