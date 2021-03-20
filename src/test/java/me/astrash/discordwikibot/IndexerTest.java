package me.astrash.discordwikibot;

import me.astrash.discordwikibot.index.LuceneIndexer;
import org.apache.lucene.queryparser.classic.ParseException;

import java.io.IOException;

public class IndexerTest {
    public static void main(String[] args) throws IOException, ParseException {
        String indexDir = "./resources/index";
        String wikiRepoDir = "./resources/wikiRepo";

        LuceneIndexer indexer = new LuceneIndexer(wikiRepoDir, indexDir);

        indexer.query("Making a pack");
        indexer.query("config packs");
        indexer.query("community packs");
        indexer.query("where can i find packs");
        indexer.query("asdkjfasdnf");
    }
}
