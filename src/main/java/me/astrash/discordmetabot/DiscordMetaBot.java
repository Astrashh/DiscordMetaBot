package me.astrash.discordmetabot;

import me.astrash.discordmetabot.discord.MessageListener;
import me.astrash.discordmetabot.index.InfoIndex;
import me.astrash.discordmetabot.index.PageIndex;
import me.astrash.discordmetabot.index.lucene.LuceneIndexer;
import me.astrash.discordmetabot.util.BasicConfigHandler;
import me.astrash.discordmetabot.util.git.GitUtil;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import org.eclipse.jgit.api.errors.GitAPIException;

import javax.security.auth.login.LoginException;
import java.io.*;
import java.util.Properties;

public class DiscordMetaBot {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(DiscordMetaBot.class);

    public static void main(String[] args) throws IOException {

        String wikiRepoDir = "./resources/wikiRepo";
        String indexDir = "./resources/index";
        String infoDir = "./resources/info";

        // Simple temporary config reader
        Properties config = new Properties();
        try {
            BasicConfigHandler.setup(config, "./resources");
        } catch (IOException e) {
            logger.error("Failed to load config!");
            e.printStackTrace();
            System.exit(-1);
        }

        // Ensuring wiki files are set up
        try {
            GitUtil.setupWikiRepo(config.getProperty("wikiURI"), wikiRepoDir, config.getProperty("wikiPullBranch"));
        } catch (GitAPIException | IOException e) {
            logger.error("Failed to set up wiki repository!");
            e.printStackTrace();
        }

        // Handles indexing the wiki and search queries
        logger.info("Indexing repository...");
        PageIndex indexer = new LuceneIndexer(wikiRepoDir, indexDir);

        logger.info("Loading information files...");
        InfoIndex infoIndex = new InfoIndex(infoDir);

        // Setting up discord bot
        try {
            setupBot(config.getProperty("botToken"), indexer, infoIndex);
        } catch (LoginException e) {
            logger.error("Failed to set up Discord bot via JDA!");
            e.printStackTrace();
        }
    }

    private static void setupBot(String token, PageIndex pageIndex, InfoIndex infoIndex) throws LoginException {

        JDABuilder builder = JDABuilder.createDefault(token);
        JDA bot = builder
                .addEventListeners(new MessageListener(pageIndex, infoIndex))
                .build();
    }
}