package me.astrash.discordmetabot;

import me.astrash.discordmetabot.index.PageIndex;
import me.astrash.discordmetabot.index.lucene.LuceneIndexer;
import org.apache.lucene.queryparser.classic.ParseException;

import java.io.IOException;

public class IndexerTest {
    public static void main(String[] args) throws IOException, ParseException {
        String indexDir = "./resources/index";
        String wikiRepoDir = "./resources/wikiRepo";

        PageIndex indexer = new LuceneIndexer(wikiRepoDir, indexDir);

        indexer.query("Making a pack");
        indexer.query("config packs");
        indexer.query("community packs");
        indexer.query("where can i find packs");
        indexer.query("asdkjfasdnf");
    }
}
