package me.astrash.discordwikibot;

import org.apache.commons.io.FilenameUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.MMapDirectory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class LuceneTest {
    public static void main(String[] args) throws IOException, ParseException {

        String indexDir = "./resources/index";
        String wikiRepoDir = "./resources/wikiRepo";

        // TODO - Add custom analysis functionality
        Analyzer analyzer = new StandardAnalyzer(); // Used in both indexing and queries

        indexWiki(wikiRepoDir, indexDir, analyzer);

        // Set up query parser
        String[] fields = new String[] { "title", "contents" };
        HashMap<String, Float> boosts = new HashMap<String, Float>();
        boosts.put("title", (float) 5);
        boosts.put("contents", (float) 1);
        QueryParser parser = new MultiFieldQueryParser(fields, analyzer, boosts);

        query("Making a pack", indexDir, parser);
        query("config packs", indexDir, parser);
        query("community packs", indexDir, parser);
        query("where can i find packs", indexDir, parser);
        query("asdkjfasdnf", indexDir, parser);
    }

    private static void indexWiki(String wikiRepoDir, String indexDir, Analyzer analyzer) throws IOException, ParseException {
        long startTime = System.nanoTime();
        // Creating the index
        MMapDirectory directory = new MMapDirectory(Paths.get(indexDir));
        IndexWriterConfig config = new IndexWriterConfig(analyzer)
                .setOpenMode(IndexWriterConfig.OpenMode.CREATE);
                // Just creates a new index regardless of if it exists
        IndexWriter writer = new IndexWriter(directory, config);

        // Index all markdown documents in wiki repo

        // TODO -  Treat subheadings within markdown files as separate documents
        //         users to search for both full pages AND subheadings within pages.

        for (String p : getFilesWithExtension(wikiRepoDir, ".md")) {
            indexDoc(writer, Paths.get(p).toAbsolutePath(), FilenameUtils.getBaseName(p));
        }

        writer.close();

        // Print index speed
        long duration = (System.nanoTime() - startTime) / 1000000;
        System.out.println("Index took " + duration + "ms");
    }

    private static void query(String input, String indexDir, QueryParser parser) throws IOException, ParseException {
        long startTime = System.nanoTime();
        IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(indexDir)));
        IndexSearcher searcher = new IndexSearcher(reader);

        Query query = parser.parse(input);

        System.out.println("========================================================");
        System.out.println("Searching for: " + input);
        System.out.println("Analyzed query: \"" + query.toString("contents") + "\"");
        int matches = searcher.count(query);

        if (matches < 1) {
            System.out.println("No matches found");
            return;
        }

        System.out.println("Found " + matches + " results:");

        // TODO - Delegate search handling outside of method
        TopDocs searchResults = searcher.search(query, matches);
        ScoreDoc[] hits = searchResults.scoreDocs;

        for (int i = 0; i < matches; i++) {
            int docNum = hits[i].doc;
            Document doc = searcher.doc(docNum);
            String title = doc.get("title").replace("-", " ");
            System.out.println(" - " + title);
        }

        reader.close();

        // Print search speed
        long duration = (System.nanoTime() - startTime) / 1000000;
        System.out.println("Query took " + duration + "ms");
    }

    private static List<String> getFilesWithExtension(String searchDir, String extension) throws IOException {
        return Files
                .walk(Paths.get(searchDir))
                .filter(Files::isRegularFile)
                .map(Path::toString) // Convert file name to a string
                .filter(fileDir -> fileDir.endsWith(extension)) // Make sure file is markdown
                .collect(Collectors.toList());
    }

    public static void indexDoc(IndexWriter writer, Path file, String title) throws IOException {
        InputStream stream = Files.newInputStream(file);

        Document document = new Document();

        Field pathField = new StringField("path", file.toString(), Field.Store.YES);
        document.add(pathField);

        Field contentsField = new TextField("contents", new BufferedReader(new InputStreamReader(stream)));
        document.add(contentsField);

        Field titleField = new TextField("title", title, Field.Store.YES);
        document.add(titleField);

        writer.addDocument(document);
    }
}
