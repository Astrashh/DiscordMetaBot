package me.astrash.discordmetabot;

import me.astrash.discordmetabot.config.ConfigHandler;
import me.astrash.discordmetabot.discord.BotHandler;
import me.astrash.discordmetabot.index.InfoIndex;
import me.astrash.discordmetabot.index.page.PageIndex;
import me.astrash.discordmetabot.index.page.lucene.LuceneIndexer;
import me.astrash.discordmetabot.util.git.GitUtil;
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
            wikiRepoPath = Paths.get(resourceDir + "/wikiRepo"),
            indexPath = Paths.get(resourceDir + "/index"),
            infoPath = Paths.get(resourceDir + "/info");

        configHandler = new ConfigHandler(Paths.get(resourceDir + "/config.yml"));

        try {
            GitUtil.setupWikiRepo(configHandler.getConfig().getWikiURI(), wikiRepoPath, configHandler.getConfig().getPullBranch());
        } catch (GitAPIException | IOException e) {
            logger.error("Failed to set up wiki repository!", e);
        }
        logger.info("Indexing repository...");
        PageIndex indexer = new LuceneIndexer(wikiRepoPath, indexPath);
        logger.info("Loading information files...");
        InfoIndex infoIndex = new InfoIndex(infoPath);
        try {
            new BotHandler(configHandler.getConfig().getDiscordBotToken(), indexer, infoIndex);
        } catch (LoginException e) {
            logger.error("Failed to set up Discord bot via JDA!", e);
        }
    }

    public static void main(String[] args) throws IOException {
        new DiscordMetaBot();
    }
}