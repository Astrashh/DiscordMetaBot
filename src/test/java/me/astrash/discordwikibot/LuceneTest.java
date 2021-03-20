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
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
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

        // Creating the index
        MMapDirectory directory = new MMapDirectory(Paths.get(indexDir));
        Analyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig config = new IndexWriterConfig(analyzer)
                .setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        IndexWriter writer = new IndexWriter(directory, config);

        getFilesWithExtension(wikiRepoDir, ".md").forEach(p ->
            {
                try {
                    indexDoc(writer, Paths.get(p).toAbsolutePath(), FilenameUtils.getBaseName(p));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        );

        writer.close();

        // Setting up queries
        IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(indexDir)));
        IndexSearcher searcher = new IndexSearcher(reader);
        String[] fields = new String[] { "title", "contents" };
        HashMap<String, Float> boosts = new HashMap<String, Float>();
        boosts.put("title", (float) 5);
        boosts.put("contents", (float) 1);
        QueryParser parser = new MultiFieldQueryParser(fields, analyzer, boosts);

        // Making a query
        query("Tree configuration", parser, searcher);
        query("Config packs", parser, searcher);
        query("biome config", parser, searcher);

        reader.close();
    }

    private static void query(String input, QueryParser parser, IndexSearcher searcher) throws ParseException, IOException {

        Query query = parser.parse(input);
        System.out.println("===========================");
        System.out.println("Searching for: " + input);
        System.out.println("Analyzed query: \"" + query.toString("contents") + "\"");
        int matches = searcher.count(query);
        System.out.println("Found " + matches + " results:");

        TopDocs searchResults = searcher.search(query, matches);
        ScoreDoc[] hits = searchResults.scoreDocs;

        for (int i = 0; i < matches; i++) {
            int docNum = hits[i].doc;
            Document doc = searcher.doc(docNum);
            String title = doc.get("title");
            System.out.println(title);
        }
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
