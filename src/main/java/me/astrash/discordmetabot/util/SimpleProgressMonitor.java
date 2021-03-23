package me.astrash.discordmetabot.util;

import org.eclipse.jgit.lib.ProgressMonitor;

public class SimpleProgressMonitor implements ProgressMonitor {

    // Taken from here
    // https://github.com/centic9/jgit-cookbook/blob/4b68d561c88904100cc8adb747dd6d0b58627969/src/main/java/org/dstadler/jgit/porcelain/CloneRemoteRepository.java

    @Override
    public void start(int totalTasks) {
        System.out.println("Starting work on " + totalTasks + " tasks");
    }

    @Override
    public void beginTask(String title, int totalWork) {
        System.out.println(" " + title + ": " + totalWork);
    }

    @Override
    public void update(int completed) {
        //System.out.print(completed + "-");
        System.out.print("-");
    }

    @Override
    public void endTask() {
        System.out.println(" Done");
    }

    @Override
    public boolean isCancelled() {
        return false;
    }
}