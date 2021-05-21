package me.astrash.discordmetabot.index.lucene;

import me.astrash.discordmetabot.index.PageIndex;
import me.astrash.discordmetabot.index.PageResult;
import me.astrash.discordmetabot.parse.MarkdownParser;
import me.astrash.discordmetabot.parse.commonmark.CommonMarkParser;
import me.astrash.discordmetabot.util.FileUtil;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
/*
 * Handles both constructing an index of markdown files, and queries of that index.
 */
public class LuceneIndexer implements PageIndex {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(LuceneIndexer.class);

    String dataPath;
    String indexPath;

    QueryParser parser;
    Analyzer analyzer;

    public LuceneIndexer(String dataPath, String indexPath) throws IOException {
        this.dataPath = dataPath;
        this.indexPath = indexPath;

        // TODO - Add custom analysis functionality
        this.analyzer = new StandardAnalyzer(); // Used in both indexing and queries

        // Set up query parser
        String[] fields = new String[]{"title", "contents"};
        HashMap<String, Float> boosts = new HashMap<>();
        boosts.put("title", (float) 5);
        boosts.put("contents", (float) 1);
        this.parser = new MultiFieldQueryParser(fields, analyzer, boosts);

        indexWiki();
    }

    @Override
    public PageResult[] query(String input) {

        try {
            long startTime = System.nanoTime();
            IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(indexPath)));
            IndexSearcher searcher = new IndexSearcher(reader);

            Query query = parser.parse(input);

            logger.debug("Analyzed query: \"" + query.toString("contents") + "\"");
            int matches = searcher.count(query);

            if (matches < 1) {
                logger.info("No matches found");
                return new LucenePageResult[0];
            }

            logger.info("Found " + matches + " results");
            ScoreDoc[] scoreDocs = searcher.search(query, matches).scoreDocs;
            PageResult[] searchResults = LucenePageResult.convertScoreDocs(scoreDocs, searcher);
            reader.close();

            long duration = (System.nanoTime() - startTime) / 1000000;
            logger.info("Query took " + duration + "ms");

            return searchResults;
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }

        // Failed query
        return new LucenePageResult[0];
    }

    /*
     * Scans through the data path and constructs a Lucene index.
     * Both entire files and individual headings inside those files
     * each get indexed as their own Lucene document inside the index.
     */
    private void indexWiki() throws IOException {
        long startTime = System.nanoTime();
        // Creating the index
        Directory directory = FSDirectory.open(Paths.get(indexPath));
        IndexWriterConfig config = new IndexWriterConfig(analyzer)
                .setOpenMode(IndexWriterConfig.OpenMode.CREATE);

        // Index all markdown documents in wiki repo
        // TODO -  Treat subheadings within markdown files as separate documents
        //         users to search for both full pages AND subheadings within pages.
        IndexWriter writer = new IndexWriter(directory, config);

        for (String p: FileUtil.getFilesWithExtension(dataPath, ".md")) {

            String baseName = FileUtil.getBaseName(p);
            if (baseName.startsWith("_")) continue; // Don't index misc github pages

            // Index file
            indexDoc(writer, p, baseName);

            // Index headings in file
            MarkdownParser parser = new CommonMarkParser();
            for (String heading: parser.getHeadings(p)) {
                indexHeader(writer, p, heading);
            }
        }
        writer.close();
        // Print index speed
        long duration = (System.nanoTime() - startTime) / 1000000;
        logger.info("Index took " + duration + "ms");
    }

    /*
     * Creates and indexes headers as Lucene documents.
     */
    private static void indexHeader(IndexWriter writer, String file, String header) throws IOException {

        Document document = new Document();

        Field typeField = new StoredField("type", "header");
        document.add(typeField);
        Field pathField = new StringField("path", file, Field.Store.YES);
        document.add(pathField);
        Field titleField = new TextField("title", header, Field.Store.YES);
        document.add(titleField);

        writer.addDocument(document);
    }

    /*
     * Creates and indexes whole files as Lucene documents.
     */
    private static void indexDoc(IndexWriter writer, String file, String title) throws IOException {

        Document document = new Document();

        Field typeField = new StoredField("type", "file");
        document.add(typeField);
        Field pathField = new StringField("path", file, Field.Store.YES);
        document.add(pathField);
        BufferedReader br = new BufferedReader(new InputStreamReader(Files.newInputStream(Paths.get(file).toAbsolutePath())));
        Field contentsField = new TextField("contents", br);
        document.add(contentsField);
        Field titleField = new TextField("title", title, Field.Store.YES);
        document.add(titleField);

        writer.addDocument(document);
    }
}
