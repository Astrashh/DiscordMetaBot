package me.astrash.discordmetabot.util;

import org.apache.commons.io.FilenameUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public final class FileUtil {
    private FileUtil(){}

    public static String getBaseName(String fileName) {
        return FilenameUtils.getBaseName(fileName);
    }

    /*
     * Returns the paths of files with the provided file extensions
     */
    public static List<String> getFilesWithExtensions(Path searchDir, String[] extensions) throws IOException {
        return Files
                .walk(searchDir)
                .filter(Files::isRegularFile)
                .map(Path::toString)
                .filter(fileDir -> {
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

    /*
     * Returns the paths of files with the provided file extension
     */
    public static List<String> getFilesWithExtension(Path searchDir, String extension) throws IOException {
        return getFilesWithExtensions(searchDir, new String[]{extension});
    }
}
