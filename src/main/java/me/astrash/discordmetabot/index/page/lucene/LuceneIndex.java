package me.astrash.discordmetabot.index.page.lucene;

import me.astrash.discordmetabot.index.page.PageIndex;
import me.astrash.discordmetabot.index.page.PageResult;
import me.astrash.discordmetabot.parse.MarkdownParser;
import me.astrash.discordmetabot.parse.commonmark.CommonMarkParser;
import me.astrash.discordmetabot.util.FileUtil;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.stream.Collectors;

/*
 * Handles both constructing an index of markdown files, and queries of that index.
 */
public class LuceneIndex implements PageIndex {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(LuceneIndex.class);

    private final Path dataPath;
    private final Path indexPath;

    private final QueryParser parser;
    private final Analyzer analyzer;

    public LuceneIndex(Path dataPath, Path indexPath) throws IOException {
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

        Directory directory = FSDirectory.open(indexPath);
        index(directory);
        directory.close();
    }

    @Override
    public PageResult[] query(String input) {
        long startTime = System.nanoTime();
        try {
            Directory directory = FSDirectory.open(indexPath);
            IndexReader reader = DirectoryReader.open(directory);
            IndexSearcher searcher = new IndexSearcher(reader);
            Query query = parser.parse(input);
            int matches = searcher.count(query);
            if (matches > 1) {
                PageResult[] searchResults = LucenePageResult.convertScoreDocs(searcher.search(query, matches).scoreDocs, searcher);
                reader.close();
                directory.close();
                long duration = (System.nanoTime() - startTime) / 1000000;
                logger.debug("Query took " + duration + "ms");
                return searchResults;
            }
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }
        // Failed query
        return new LucenePageResult[0];
    }

    private void index(Directory directory) throws IOException {
        IndexWriterConfig config = new IndexWriterConfig(analyzer).setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        IndexWriter writer = new IndexWriter(directory, config);
        writeIndex(writer);
        writer.close();
    }

    private void writeIndex(IndexWriter writer) throws IOException {
        for (String path : FileUtil.getFilesWithExtension(dataPath, ".md")) {
            String fileName = FileUtil.getBaseName(path);
            if (fileName.startsWith("_")) continue; // Don't index misc github pages
            writer.addDocument(DocUtil.createFileDoc(path, fileName));
            // Index headings
            MarkdownParser parser = new CommonMarkParser(new FileInputStream(path));
            writer.addDocuments(parser.getHeadings(4).stream()
                    .map(header -> DocUtil.createHeaderDoc(path, header))
                    .collect(Collectors.toList())
            );
        }
    }

    private static class DocUtil {
        private static Document createHeaderDoc(String file, String header) {
            Document document = new Document();
            Field typeField = new StoredField("type", "header");
            document.add(typeField);
            Field pathField = new StringField("path", file, Field.Store.YES);
            document.add(pathField);
            Field titleField = new TextField("title", header, Field.Store.YES);
            document.add(titleField);
            return document;
        }

        private static Document createFileDoc(String file, String title) throws IOException {
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
            return document;
        }
    }
}
