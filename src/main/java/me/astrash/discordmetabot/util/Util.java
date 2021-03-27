package me.astrash.discordmetabot.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class Util {
    private Util(){}

    /*
     * Returns the paths of files with a certain file extension
     */
    public static List<String> getFilesWithExtensions(String searchDir, String[] extensions) throws IOException {
        return Files
                .walk(Paths.get(searchDir))
                .filter(Files::isRegularFile)
                .map(Path::toString) // Convert file name to a string
                .filter(fileDir -> { // Make sure file ends with extension
                    // Probably a much better way of doing this
                    boolean endsWithExtension = false;
                    for (String extension: extensions) {
                        if (fileDir.endsWith(extension)) {
                            endsWithExtension = true;
                            break;
                        }
                    }
                    return endsWithExtension;
                })
                .collect(Collectors.toList());
    }

    public static List<String> getFilesWithExtension(String searchDir, String extension) throws IOException {
        return getFilesWithExtensions(searchDir, new String[]{extension});
    }
}
