import me.astrash.discordmetabot.index.page.PageIndex;
import me.astrash.discordmetabot.index.page.lucene.LuceneIndexer;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class IndexerTest {
    @Test
    public void name() throws IOException {

        Path
            indexPath = Paths.get("./resources/index"),
            wikiRepoPath = Paths.get("./resources/wikiRepo");

        PageIndex indexer = new LuceneIndexer(wikiRepoPath, indexPath);

        indexer.query("Making a pack");
        indexer.query("config packs");
        indexer.query("community packs");
        indexer.query("where can i find packs");
        indexer.query("asdkjfasdnf");
    }
}
