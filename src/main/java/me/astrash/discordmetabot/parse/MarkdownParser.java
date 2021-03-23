package me.astrash.discordmetabot.parse;

import java.io.IOException;
import java.util.List;

public interface MarkdownParser {

    public List<String> getHeadings(String filePath) throws IOException;

}
