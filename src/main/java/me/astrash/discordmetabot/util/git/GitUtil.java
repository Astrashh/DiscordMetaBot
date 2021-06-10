package me.astrash.discordmetabot.util.git;

import me.astrash.discordmetabot.util.FileUtil;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.RepositoryNotFoundException;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.transport.FetchResult;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class GitUtil {
    private GitUtil(){}

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(GitUtil.class);

    /*
     * Ensures that there is an up to date local copy of a repository.
     */
    public static void setupRepo(String URI, Path repoPath, String pullBranch) throws IOException, GitAPIException {

        Files.createDirectories(repoPath);
        File repoFolder = repoPath.toFile();

        // Attempt to open repository.
        try (Git git = Git.open(repoFolder)) {
            logger.info("Found existing repository in " + repoPath);

            // $ git fetch origin
            logger.info("Fetching from " + URI);
            FetchResult fetchResult = git.fetch()
                    .setRemote("origin")
                    .setProgressMonitor(new GitProgressMonitor())
                    .call();
            fetchResult.getTrackingRefUpdates().forEach(System.out::println);

            // $ git reset --hard origin/branch
            logger.info("Hard resetting to remote tracking branch " + pullBranch);
            git.reset()
                    .setMode(ResetCommand.ResetType.HARD)
                    .setRef("origin/" + pullBranch)
                    .setProgressMonitor(new GitProgressMonitor())
                    .call();
        } catch (RepositoryNotFoundException e) {
            // If a repository can't be found, try to clone from remote.
            logger.info("Could not find git repository in " + repoPath);
            cloneRepo(repoPath, URI);
        }
    }

    private static void cloneRepo(Path path, String URI) throws IOException, GitAPIException {
        logger.info("Cloning " + URI + " to " + path);
        // Clear files for clone.
        logger.info("Clearing files inside " + path);
        FileUtil.cleanDirectory(path.toFile());

        // $ git clone URI
        logger.info("Starting clone...");
        Git git = Git.cloneRepository()
                .setURI(URI)
                .setDirectory(path.toFile())
                .setProgressMonitor(new GitProgressMonitor())
                .call();

        // Set remote tracking branch
        StoredConfig config = git.getRepository().getConfig();
        config.setString("remote", "origin", "url", URI);
        config.save();
        logger.info("Clone successful!");
    }
}
