package me.astrash.discordmetabot.util.git;

import me.astrash.discordmetabot.DiscordMetaBot;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.RepositoryNotFoundException;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.transport.FetchResult;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public final class GitUtil {
    private GitUtil(){}

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(GitUtil.class);

    /*
     * Ensures that there is an up to date local copy
     * of a repository storing GitHub wiki pages.
     */
    public static void setupWikiRepo(String wikiURI, String wikiDir, String wikiBranch) throws IOException, GitAPIException {

        // Create directory to store wiki repository if it doesn't exist.
        Files.createDirectories(Paths.get(wikiDir));
        File wikiFolder = new File(wikiDir);

        // Attempt to open repository.
        try (Git git = Git.open(wikiFolder)) {
            logger.info("Found repository in " + wikiDir);

            // $ git fetch origin
            logger.debug("JGit - Fetching from remote");
            FetchResult fetchResult = git.fetch()
                    .setRemote("origin")
                    .setProgressMonitor(new SimpleProgressMonitor())
                    .call();
            fetchResult.getTrackingRefUpdates().forEach(System.out::println);

            // $ git reset --hard origin/branch
            logger.debug("JGit - Hard resetting to remote tracking branch");
            git.reset()
                    .setMode(ResetCommand.ResetType.HARD)
                    .setRef("origin/" + wikiBranch)
                    .setProgressMonitor(new SimpleProgressMonitor())
                    .call();
        } catch (RepositoryNotFoundException e) {

            // If a repository can't be found, try to clone from remote.
            logger.info("Could not find git repository in " + wikiDir);

            // Clear files for clone.
            logger.debug("Clearing files inside " + wikiDir);
            FileUtils.cleanDirectory(wikiFolder);

            // $ git clone wikiURI
            logger.debug("JGit - Cloning " + wikiURI + " to " + wikiDir);
            Git git = Git.cloneRepository()
                    .setURI(wikiURI)
                    .setDirectory(wikiFolder)
                    .setProgressMonitor(new SimpleProgressMonitor())
                    .call();

            // Set remote tracking branch
            StoredConfig config = git.getRepository().getConfig();
            config.setString("remote", "origin", "url", wikiURI);
            config.save();
            logger.debug("Clone successful!");
        }
    }
}
