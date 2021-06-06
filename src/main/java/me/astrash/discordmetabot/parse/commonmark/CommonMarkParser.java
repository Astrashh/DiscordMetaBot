package me.astrash.discordmetabot.parse.commonmark;

import me.astrash.discordmetabot.parse.MarkdownParser;
import org.commonmark.node.AbstractVisitor;
import org.commonmark.node.Heading;
import org.commonmark.node.Node;
import org.commonmark.node.Text;
import org.commonmark.parser.Parser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class CommonMarkParser implements MarkdownParser {

    private final InputStream stream;

    public CommonMarkParser(InputStream stream) {
        this.stream = stream;
    }

    @Override
    public List<String> getHeadings(int headerDepth) throws IOException {
        Parser parser = Parser.builder().build();
        InputStreamReader reader = new InputStreamReader(stream);
        Node document = parser.parseReader(reader);
        reader.close();
        HeaderVisitor visitor = new HeaderVisitor(headerDepth);
        document.accept(visitor);
        return visitor.getHeadings();
    }

    private static class HeaderVisitor extends AbstractVisitor {

        private final int headerDepth;

        public HeaderVisitor(int headerDepth) {
            this.headerDepth = headerDepth;
        }

        List<String> headings = new ArrayList<>();
        @Override
        public void visit(Heading node) {
            if (node.getLevel() <= headerDepth) { // Only index higher level headings
                Node child = node.getFirstChild();
                if (child instanceof Text) {
                    Text text = (Text) child;
                    headings.add(text.getLiteral());
                }
            }
            visitChildren(node);
        }
        public List<String> getHeadings() {
            return headings;
        }
    }
}
