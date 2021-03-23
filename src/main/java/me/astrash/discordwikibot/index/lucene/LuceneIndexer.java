package me.astrash.discordwikibot.index.lucene;

import me.astrash.discordwikibot.index.Indexer;
import org.apache.commons.io.FilenameUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.MMapDirectory;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/*
 * Handles both constructing an index of markdown files, and queries of that index.
 */
public class LuceneIndexer implements Indexer {

    String dataPath;
    String indexPath;

    QueryParser parser;
    Analyzer analyzer;

    public LuceneIndexer(String dataPath, String indexPath) throws IOException, ParseException {
        this.dataPath = dataPath;
        this.indexPath = indexPath;

        // TODO - Add custom analysis functionality
        this.analyzer = new StandardAnalyzer(); // Used in both indexing and queries

        // Set up query parser
        String[] fields = new String[] { "title", "contents" };
        HashMap<String, Float> boosts = new HashMap<String, Float>();
        boosts.put("title", (float) 5);
        boosts.put("contents", (float) 1);
        this.parser = new MultiFieldQueryParser(fields, analyzer, boosts);

        indexWiki();
    }

    /*
     * Scans through the data path and constructs a Lucene index.
     * Both entire files and individual headings inside those files
     * each get indexed as their own Lucene document inside the index.
     */
    private void indexWiki() throws IOException, ParseException {
        long startTime = System.nanoTime();
        // Creating the index
        MMapDirectory directory = new MMapDirectory(Paths.get(indexPath));
        IndexWriterConfig config = new IndexWriterConfig(analyzer)
                .setOpenMode(IndexWriterConfig.OpenMode.CREATE);
                // Just creates a new index regardless of if it exists.
                // Could probably make it update an existing index later.
        IndexWriter writer = new IndexWriter(directory, config);

        // Index all markdown documents in wiki repo

        // TODO -  Treat subheadings within markdown files as separate documents
        //         users to search for both full pages AND subheadings within pages.

        for (String p : getFilesWithExtension(dataPath, ".md")) {
            String baseName = FilenameUtils.getBaseName(p);
            if (baseName.startsWith("_")) continue;

            // Index file
            indexDoc(writer, p, baseName);

            // Index headings
            Parser parser = Parser.builder().build();
            InputStreamReader reader = new InputStreamReader(new FileInputStream(p));
            Node document = parser.parseReader(reader);
            HeaderVisitor visitor = new HeaderVisitor();
            document.accept(visitor);
            for (String heading : visitor.getHeadings()) {
                indexHeader(writer, p, heading);
            }
        }

        writer.close();

        // Print index speed
        long duration = (System.nanoTime() - startTime) / 1000000;
        System.out.println("Index took " + duration + "ms");
    }

    @Override
    public LuceneQueryResult[] query(String input) {

        try {
            long startTime = System.nanoTime();
            IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(indexPath)));
            IndexSearcher searcher = new IndexSearcher(reader);

            Query query = parser.parse(input);

            System.out.println("Analyzed query: \"" + query.toString("contents") + "\"");
            int matches = searcher.count(query);

            if (matches < 1) {
                System.out.println("No matches found");
                return new LuceneQueryResult[0];
            }

            System.out.println("Found " + matches + " results:");
            ScoreDoc[] scoreDocs = searcher.search(query, matches).scoreDocs;
            LuceneQueryResult[] searchResults = LuceneQueryResult.convertScoreDocs(scoreDocs, searcher);
            reader.close();

            // Print search speed
            long duration = (System.nanoTime() - startTime) / 1000000;
            System.out.println("Query took " + duration + "ms");

            return searchResults;
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static List<String> getFilesWithExtension(String searchDir, String extension) throws IOException {
        return Files
                .walk(Paths.get(searchDir))
                .filter(Files::isRegularFile)
                .map(Path::toString) // Convert file name to a string
                .filter(fileDir -> fileDir.endsWith(extension)) // Make sure file is markdown
                .collect(Collectors.toList());
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
