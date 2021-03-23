package me.astrash.discordmetabot.parse.commonmark;

import me.astrash.discordmetabot.parse.MarkdownParser;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class CommonMarkParser implements MarkdownParser {

    @Override
    public List<String> getHeadings(String filePath) throws IOException {
        Parser parser = Parser.builder().build();
        InputStreamReader reader = new InputStreamReader(new FileInputStream(filePath));
        Node document = parser.parseReader(reader);
        HeaderVisitor visitor = new HeaderVisitor();
        document.accept(visitor);

        return visitor.getHeadings();
    }
}
