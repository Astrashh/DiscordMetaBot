import me.astrash.discordmetabot.util.CommandUtil;
import org.junit.Test;

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
}
