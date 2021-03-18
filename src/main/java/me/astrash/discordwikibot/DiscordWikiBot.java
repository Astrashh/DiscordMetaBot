package me.astrash.discordwikibot;

import me.astrash.discordwikibot.util.BasicConfigHandler;
import me.astrash.discordwikibot.util.SimpleProgressMonitor;
import net.dv8tion.jda.api.JDABuilder;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.RepositoryNotFoundException;
import org.eclipse.jgit.lib.Repository;

import javax.security.auth.login.LoginException;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

public class DiscordWikiBot {
    public static void main(String[] args) {

        // Simple temporary config reader
        Properties config = new Properties();
        try {
            BasicConfigHandler.setup(config, "./resources/config.properties");
        } catch (IOException e) {
            System.out.println("Failed to load config!");
            e.printStackTrace();
            System.exit(-1);
        }

        String wikiRepoDir = "./resources/wikiRepo";

        // Ensuring wiki files are set up
        try {
            setupWikiRepo(config.getProperty("wikiURI"), wikiRepoDir);
        } catch (GitAPIException | IOException e) {
            e.printStackTrace();
        }

        // Listing off all markdown file names
        List<String> pages = null;
        try {
            pages = getFilesWithExtension(wikiRepoDir, ".md").stream()
                    .map(fileDir -> fileDir.substring(0, fileDir.lastIndexOf('.'))) // Strip file extension
                    .collect(Collectors.toList());

            pages.forEach(System.out::println);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Setting up discord bot
        try {
            setupBot(config.getProperty("botToken"));
        } catch (LoginException e) {
            System.out.print("Failed to set up Discord bot via JDA!");
            e.printStackTrace();
        }
    }

    private static List<String> getFilesWithExtension(String searchDir, String extension) throws IOException {
        return Files.walk(Paths.get(searchDir))
                .filter(Files::isRegularFile)
                .map(Path::getFileName)
                .map(Path::toString) // Convert file name to a string
                .filter(fileDir -> fileDir.endsWith(extension)) // Make sure file is markdown
                .collect(Collectors.toList());
    }

    private static void setupBot(String token) throws LoginException {
        JDABuilder builder = JDABuilder.createDefault(token);
        builder.addEventListeners(new MessageListener());
        builder.build();
    }

    private static void setupWikiRepo(String wikiURI, String wikiDir) throws IOException, GitAPIException {

        Path wikiPath = Paths.get(wikiDir);
        File wikiFile = new File(wikiDir);

        // Create directory to store wiki repository if it doesn't exist.
        Files.createDirectories(wikiPath);

        Repository repository;

        // Attempt to open repository.
        try (Git git = Git.open(wikiFile)) {
            repository = git.getRepository();
            System.out.println("Found repository in " + wikiDir);

        } catch (RepositoryNotFoundException e) {
            // If a repository can't be found, try to clone from remote.
            System.out.println("Could not find git repository in " + wikiDir);

            // Clear files for clone.
            System.out.println("Clearing files inside " + wikiDir);
            FileUtils.cleanDirectory(wikiFile);

            // Attempt to clone repository.
            System.out.println("Cloning " + wikiURI + " to " + wikiDir);
            Git git = Git.cloneRepository()
                    .setURI(wikiURI)
                    .setDirectory(wikiFile)
                    .setProgressMonitor(new SimpleProgressMonitor())
                    .call();

            repository = git.getRepository();
            System.out.println("Clone successful!");
        }
    }
}