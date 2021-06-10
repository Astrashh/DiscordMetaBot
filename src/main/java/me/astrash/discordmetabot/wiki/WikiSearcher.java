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

    public final static int MAX_RESULTS_DEFAULT = 3;
    public final static int MAX_RESULTS_PRIVATE = 25;
    public final static int MAX_RESULTS_PUBLIC = 1;

    Index<String, PageResult[]> pageIndex;

    // TODO - Separate embed formatting to another class
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

    public MessageEmbed search(String query, int maxResults) {
        if (query.length() > MessageEmbed.TITLE_MAX_LENGTH) return tooLongEmbed;
        long queryStart = System.nanoTime();
        PageResult[] queryResults = pageIndex.query(query);
        float queryTime = Duration.ofNanos(System.nanoTime() - queryStart).toMillis();
        if (queryResults.length < 1) return warnNoResults(query);
        return buildResultsEmbed(queryResults, queryTime, query, maxResults);
    }

    public MessageEmbed search(String query) {
        return search(query, MAX_RESULTS_DEFAULT);
    }

    private MessageEmbed buildResultsEmbed(PageResult[] queryResults, float queryTime, String args, int maxResults) {
        EmbedBuilder builder = new EmbedBuilder(defaultEmbedBuilder);
        //builder.setFooter("Found in " + Math.round(queryTime) + "ms");
        PageResult[] resultsToDisplay = clampResults(queryResults, maxResults);
        addHeadingToEmbed(builder, resultsToDisplay, queryResults, args, maxResults);
        addResultsToEmbed(builder, resultsToDisplay);
        return builder.build();
    }

    private void addHeadingToEmbed(EmbedBuilder builder, PageResult[] displayResults, PageResult[] results, String args, int maxResults) {
        if (maxResults == 1) {
            builder.appendDescription("*:mag: Most relevant result for '" + args + "'*");
        } else {
            String plural = results.length == 1 ? "" : "s";
            builder.setTitle(":mag: Found " + results.length + " relevant page" + plural + " for '" + args + "'");
            if (results.length > maxResults) {
                builder.appendDescription("*Displaying the first " + displayResults.length + " most relevant results*");
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
