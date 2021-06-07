package me.astrash.discordmetabot.wiki;

import me.astrash.discordmetabot.config.ConfigHandler;
import me.astrash.discordmetabot.index.Index;
import me.astrash.discordmetabot.index.page.PageResult;
import me.astrash.discordmetabot.util.StringUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.time.Duration;
import java.util.Arrays;

public class WikiSearcher {

    private final static int EMBED_COLOR = 0x2F3136;
    private final static int EMBED_COLOR_WARNING = 0xF8C300;
    private final static int EMBED_COLOR_ERROR = 0xDD0000;
    private final static int MAX_RESULTS = 3;

    Index<String, PageResult[]> pageIndex;

    // TODO - Replace with config defined embed templates
    private final EmbedBuilder defaultEmbedBuilder;
    private final EmbedBuilder noResultsEmbedBuilder;
    private final MessageEmbed tooLongEmbed;

    public WikiSearcher(Index<String, PageResult[]> pageIndex, ConfigHandler configHandler) {
        this.pageIndex = pageIndex;
        defaultEmbedBuilder = new EmbedBuilder()
                .setThumbnail(configHandler.getConfig().getWikiSearchEmbedImage())
                .setColor(EMBED_COLOR);

        String suggestions =
                "- Make sure that all words are spelled correctly.\n" +
                        "- Try different keywords.\n" +
                        "- Try more general keywords.\n";
        noResultsEmbedBuilder = new EmbedBuilder(defaultEmbedBuilder)
                .setTitle(":warning: Your search did not match any pages!")
                .addField("Suggestions", suggestions, false)
                .setColor(EMBED_COLOR_WARNING);

        tooLongEmbed = new EmbedBuilder(defaultEmbedBuilder)
                .setTitle(":exclamation: Your requested search is too long")
                .setColor(EMBED_COLOR_ERROR)
                .setDescription("Please use a shorter query!")
                .build();
    }

    public MessageEmbed search(String query) {
        if (query.length() > MessageEmbed.TITLE_MAX_LENGTH) return tooLongEmbed;
        long queryStart = System.nanoTime();
        PageResult[] queryResults = pageIndex.query(query);
        float queryTime = Duration.ofNanos(System.nanoTime() - queryStart).toMillis();
        if (queryResults.length < 1) return warnNoResults(query);
        return buildResultsEmbed(queryResults, queryTime, query);
    }

    private MessageEmbed buildResultsEmbed(PageResult[] queryResults, float queryTime, String args) {
        EmbedBuilder builder = new EmbedBuilder(defaultEmbedBuilder);
        builder.setFooter("Found in " + Math.round(queryTime) + "ms");
        PageResult[] resultsToDisplay = clampResults(queryResults, MAX_RESULTS);
        addHeadingToEmbed(builder, resultsToDisplay, queryResults);
        addResultsToEmbed(builder, resultsToDisplay);
        return builder.build();
    }

    private void addHeadingToEmbed(EmbedBuilder builder, PageResult[] displayResults, PageResult[] results) {
        if (results.length == 1) {
            builder.setTitle(":mag: Found 1 relevant page");
        } else {
            builder.setTitle(":mag: Found " + results.length + " relevant pages");
            if (results.length > MAX_RESULTS) {
                builder.appendDescription("*Displaying the first " + displayResults.length + " most relevant results:*");
            } else {
                builder.appendDescription("\u200B"); // Add gap between heading and results
            }
        }
    }

    private void addResultsToEmbed(EmbedBuilder builder, PageResult[] results) {
        Arrays.stream(results).forEachOrdered(result -> builder.addField(
                StringUtil.clampString(":page_facing_up: " + result.getHeading(), MessageEmbed.TITLE_MAX_LENGTH),
                StringUtil.clampString(result.getDescription(), MessageEmbed.VALUE_MAX_LENGTH),
                false
        ));
    }

    private PageResult[] clampResults(PageResult[] results, int max) {
        return Arrays.copyOfRange(results, 0, Math.min(max, results.length));
    }

    private MessageEmbed warnNoResults(String args) {
        EmbedBuilder builder = new EmbedBuilder(noResultsEmbedBuilder);
        builder.setTitle(":warning: Your search '" + args + "' did not match any pages!");
        return builder.build();
    }
}
