import me.astrash.discordmetabot.util.CommandUtil;
import me.astrash.discordmetabot.util.discord.MessageUtil;
import org.junit.Test;

import static org.junit.Assert.*;

public class CommandUtilTest {

    @Test
    public void matchedPrefix() {
        String prefix = "prefix";
        StringBuilder input = new StringBuilder("prefixsome text");
        System.out.println("Has prefix: " + CommandUtil.consumePrefix(input, prefix));
        System.out.println("Buffer: " + input);
    }

    @Test
    public void unmatchedPrefix() {
        String prefix = "prefix";
        StringBuilder input = new StringBuilder("invalidprefixsome text");
        System.out.println("Has prefix: " + CommandUtil.consumePrefix(input, prefix));
        System.out.println("Buffer: " + input);
    }

    @Test
    public void fencedCodeBlockTest() {
        // Empty code blocks should be null
        assertNull(MessageUtil.getFirstCodeBlock(
                "``````"
        ));

        // Single line codeblocks should return exact contents
        assertEquals("code", MessageUtil.getFirstCodeBlock(
                "```code```"
        ));

        // Same as above with just newlines
        assertEquals("\n", MessageUtil.getFirstCodeBlock(
                "```\n" +
                "```"
        ));

        // Languages should be ignored, given they end with a newline
        assertEquals("code\n", MessageUtil.getFirstCodeBlock(
                "```language\n" +
                "code\n" +
                "```"
        ));

        // Closing delimiter should not need its own line
        assertEquals("code", MessageUtil.getFirstCodeBlock(
                "```language\n" +
                "code```"
        ));

        // Languages must be immediately after opening delimiter,
        // otherwise they should be treated as a part of contents
        assertEquals(" notlanguage\ncode\n", MessageUtil.getFirstCodeBlock(
                "``` notlanguage\n" +
                "code\n" +
                "```"
        ));

        // Opening delimiter does not need to be at beginning of a line
        assertEquals("code\n", MessageUtil.getFirstCodeBlock(
                "a ```language\n" +
                "code\n" +
                "```"
        ));
    }
}
