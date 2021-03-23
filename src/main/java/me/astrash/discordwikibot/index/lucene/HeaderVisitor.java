package me.astrash.discordwikibot.index.lucene;

import org.commonmark.node.AbstractVisitor;
import org.commonmark.node.Heading;
import org.commonmark.node.Node;
import org.commonmark.node.Text;

import java.util.ArrayList;
import java.util.List;

/*
 * Function is to return a list of headings within a CommonMark node.
 */
class HeaderVisitor extends AbstractVisitor {

    List<String> headings = new ArrayList<>();

    @Override
    public void visit(Heading node) {
        if (node.getLevel() <= 4) { // Only index higher level headings
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