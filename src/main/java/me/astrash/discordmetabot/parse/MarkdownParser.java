package me.astrash.discordmetabot.parse;

import java.io.IOException;
import java.util.List;

public interface MarkdownParser {

    List<String> getHeadings(int headerDepth) throws IOException;

}
