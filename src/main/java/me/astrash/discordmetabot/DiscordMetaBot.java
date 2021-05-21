package me.astrash.discordmetabot;

import me.astrash.discordmetabot.config.Config;
import me.astrash.discordmetabot.index.InfoIndex;
import me.astrash.discordmetabot.index.PageIndex;
import me.astrash.discordmetabot.index.lucene.LuceneIndexer;
import me.astrash.discordmetabot.config.ConfigHandler;
import me.astrash.discordmetabot.discord.BotHandler;
import me.astrash.discordmetabot.util.git.GitUtil;
import org.eclipse.jgit.api.errors.GitAPIException;

import javax.security.auth.login.LoginException;
import java.io.*;

public class DiscordMetaBot {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(DiscordMetaBot.class);

    public DiscordMetaBot() throws IOException {
        String resourceDir = "./resources";
        String wikiRepoDir = resourceDir + "/wikiRepo";
        String indexDir = resourceDir + "/index";
        String infoDir = resourceDir + "/info";

        Config config = ConfigHandler.setup(resourceDir);
        try {
            GitUtil.setupWikiRepo(config.getWikiURI(), wikiRepoDir, config.getPullBranch());
        } catch (GitAPIException | IOException e) {
            logger.error("Failed to set up wiki repository!", e);
        }
        logger.info("Indexing repository...");
        PageIndex indexer = new LuceneIndexer(wikiRepoDir, indexDir);
        logger.info("Loading information files...");
        InfoIndex infoIndex = new InfoIndex(infoDir);
        try {
            new BotHandler(config.getDiscordBotToken(), indexer, infoIndex);
        } catch (LoginException e) {
            logger.error("Failed to set up Discord bot via JDA!", e);
        }
    }

    public static void main(String[] args) throws IOException {
        new DiscordMetaBot();
    }
}