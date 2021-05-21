package me.astrash.discordmetabot.util.git;

import org.eclipse.jgit.lib.ProgressMonitor;

/*
 * Taken from here
 * https://github.com/centic9/jgit-cookbook/blob/4b68d561c88904100cc8adb747dd6d0b58627969/src/main/java/org/dstadler/jgit/porcelain/CloneRemoteRepository.java
 */
public class SimpleProgressMonitor implements ProgressMonitor {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(SimpleProgressMonitor.class);

    @Override
    public void start(int totalTasks) {
        logger.debug("Starting work on " + totalTasks + " tasks");
    }

    @Override
    public void beginTask(String title, int totalWork) {
        logger.debug(" " + title + ": " + totalWork);
    }

    @Override
    public void update(int completed) {
        logger.debug("-");
    }

    @Override
    public void endTask() {
        logger.debug(" Done");
    }

    @Override
    public boolean isCancelled() {
        return false;
    }
}