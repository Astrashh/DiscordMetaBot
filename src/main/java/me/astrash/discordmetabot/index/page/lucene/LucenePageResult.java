package me.astrash.discordmetabot.index.page.lucene;

import me.astrash.discordmetabot.index.page.PageResult;
import org.apache.commons.io.FilenameUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;

import java.io.IOException;

public class LucenePageResult implements PageResult {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(LuceneIndex.class);

    private String heading;
    private String description;

    private final String anchorRegex = "[^a-zA-Z0-9 ]";

    /*
     * Container class for holding information for each query hit
     */
    public LucenePageResult(ScoreDoc scoreDoc, IndexSearcher searcher, String wikiURL) throws IOException {

        Document doc = searcher.doc(scoreDoc.doc);

        String fileName = FilenameUtils.getBaseName(doc.get("path"));
        String pageURL = wikiURL + "/" + fileName.replace(" ", "-");

        if (doc.get("type").equals("header")) {
            // If the indexed document is a header
            setHeading(fileName.replace("-", " ") + " - " + doc.get("title"));
            String description = pageURL + "#" + doc.get("title")
                    .replaceAll(anchorRegex, "")
                    .replace(" ", "-")
                    .toLowerCase();
            setDescription(description);
        } else if (doc.get("type").equals("file")) {
            // If the indexed document is a file
            String title = doc.get("title").replace("-", " ");
            setHeading(title);
            setDescription(pageURL);
        } else {
            // Concern
            logger.error("A document without a valid type was returned in a query!");
        }
    }

    /*
     * Returns QueryDisplays from an array of ScoreDocs (the results of a query)
     */
    public static LucenePageResult[] convertScoreDocs(ScoreDoc[] docs, IndexSearcher searcher, String wikiURL) throws IOException {

        // TODO - Add fancy logic to combine file displays with header displays

        LucenePageResult[] displays = new LucenePageResult[docs.length];

        for (int i = 0; i < docs.length; i++) {
            displays[i] = new LucenePageResult(docs[i], searcher, wikiURL);
        }

        return displays;
    }

    @Override
    public String getHeading() { return heading; }

    @Override
    public String getDescription() { return description; }

    private void setHeading(String heading) { this.heading = heading; }

    private void setDescription(String description) { this.description = description; }
}