package me.astrash.discordwikibot;

import me.astrash.discordwikibot.discord.MessageListener;
import me.astrash.discordwikibot.index.Indexer;
import me.astrash.discordwikibot.index.lucene.LuceneIndexer;
import me.astrash.discordwikibot.util.BasicConfigHandler;
import me.astrash.discordwikibot.util.SimpleProgressMonitor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import org.apache.commons.io.FileUtils;
import org.apache.lucene.queryparser.classic.ParseException;
import org.eclipse.jgit.api.*;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.RepositoryNotFoundException;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.transport.FetchResult;

import javax.security.auth.login.LoginException;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public class DiscordWikiBot {
    public static void main(String[] args) throws IOException, ParseException {

        String wikiRepoDir = "./resources/wikiRepo";
        String indexDir = "./resources/index";

        // Simple temporary config reader
        Properties config = new Properties();
        try {
            BasicConfigHandler.setup(config, "./resources");
        } catch (IOException e) {
            System.err.println("Failed to load config!");
            e.printStackTrace();
            System.exit(-1);
        }

        // Ensuring wiki files are set up
        try {
            setupWikiRepo(config.getProperty("wikiURI"), wikiRepoDir, config.getProperty("wikiPullBranch"));
        } catch (GitAPIException | IOException e) {
            System.err.println("Failed to set up wiki repository!");
            e.printStackTrace();
        }

        // Handles indexing the wiki and search queries
        System.out.println("Indexing repository...");
        Indexer indexer = new LuceneIndexer(wikiRepoDir, indexDir);

        // Setting up discord bot
        try {
            setupBot(config.getProperty("botToken"), indexer);
        } catch (LoginException e) {
            System.err.print("Failed to set up Discord bot via JDA!");
            e.printStackTrace();
        }
    }

    private static void setupBot(String token, Indexer indexer) throws LoginException {
        JDABuilder builder = JDABuilder.createDefault(token);
        JDA bot = builder
                .addEventListeners(new MessageListener(indexer))
                .build();
    }

    /*
     * Ensures that there is an up to date local copy
     * of a repository storing GitHub wiki pages.
     */

    // TODO - Decouple wiki repo handling into a separate class

    private static void setupWikiRepo(String wikiURI, String wikiDir, String wikiBranch) throws IOException, GitAPIException {

        // Create directory to store wiki repository if it doesn't exist.
        Files.createDirectories(Paths.get(wikiDir));

        File wikiFolder = new File(wikiDir);
        // Attempt to open repository.
        try (Git git = Git.open(wikiFolder)) {
            System.out.println("Found repository in " + wikiDir);

            // $ git fetch origin
            System.out.println("JGit - Fetching from remote");
            FetchResult fetchResult = git.fetch()
                    .setRemote("origin")
                    .setProgressMonitor(new SimpleProgressMonitor())
                    .call();
            fetchResult.getTrackingRefUpdates().forEach(System.out::println);

            // $ git reset --hard origin/branch
            System.out.println("JGit - Hard resetting to remote tracking branch");
            git.reset()
                    .setMode(ResetCommand.ResetType.HARD)
                    .setRef("origin/" + wikiBranch)
                    .setProgressMonitor(new SimpleProgressMonitor())
                    .call();

        } catch (RepositoryNotFoundException e) {
            // If a repository can't be found, try to clone from remote.
            System.out.println("Could not find git repository in " + wikiDir);

            // Clear files for clone.
            System.out.println("Clearing files inside " + wikiDir);
            FileUtils.cleanDirectory(wikiFolder);

            // $ git clone wikiURI
            System.out.println("JGit - Cloning " + wikiURI + " to " + wikiDir);
            Git git = Git.cloneRepository()
                    .setURI(wikiURI)
                    .setDirectory(wikiFolder)
                    .setProgressMonitor(new SimpleProgressMonitor())
                    .call();

            // Set remote tracking branch
            StoredConfig config = git.getRepository().getConfig();
            config.setString("remote", "origin", "url", wikiURI);
            config.save();

            System.out.println("Clone successful!");
        }
    }
}