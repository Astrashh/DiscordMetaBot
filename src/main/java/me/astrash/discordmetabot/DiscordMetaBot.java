package me.astrash.discordmetabot;

import com.dfsek.tectonic.exception.ConfigException;
import me.astrash.discordmetabot.config.Config;
import me.astrash.discordmetabot.discord.MessageListener;
import me.astrash.discordmetabot.index.InfoIndex;
import me.astrash.discordmetabot.index.PageIndex;
import me.astrash.discordmetabot.index.lucene.LuceneIndexer;
import me.astrash.discordmetabot.config.ConfigHandler;
import me.astrash.discordmetabot.util.git.GitUtil;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import org.eclipse.jgit.api.errors.GitAPIException;

import javax.security.auth.login.LoginException;
import java.io.*;

public class DiscordMetaBot {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(DiscordMetaBot.class);

    public DiscordMetaBot() throws IOException {
        String wikiRepoDir = "./resources/wikiRepo";
        String indexDir = "./resources/index";
        String infoDir = "./resources/info";
        Config config = ConfigHandler.setup("./resources");
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
            JDAUtil.setupBot(config.getDiscordBotToken(), indexer, infoIndex);
        } catch (LoginException e) {
            logger.error("Failed to set up Discord bot via JDA!", e);
        }
    }

    public static void main(String[] args) throws IOException {
        new DiscordMetaBot();
    }
}