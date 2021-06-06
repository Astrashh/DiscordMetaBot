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

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(WikiSearcher.class);

    private final static int EMBED_COLOR = 0x2F3136;
    private final static int EMBED_COLOR_WARNING = 0xF8C300;
    private final static int EMBED_COLOR_ERROR = 0xDD0000;
    private final static int MAX_RESULTS = 3;

    Index<String, PageResult[]> pageIndex;

    private final EmbedBuilder defaultEmbedBuilder;
    private final EmbedBuilder noResultsEmbedBuilder;
    private final MessageEmbed tooLongEmbed;

    public WikiSearcher(Index<String, PageResult[]> pageIndex, ConfigHandler configHandler) {
        this.pageIndex = pageIndex;

        defaultEmbedBuilder = new EmbedBuilder();
        defaultEmbedBuilder
                .setThumbnail(configHandler.getConfig().getWikiSearchEmbedImage())
                .setColor(EMBED_COLOR);

        noResultsEmbedBuilder = new EmbedBuilder(defaultEmbedBuilder);
        String suggestions =
                "- Make sure that all words are spelled correctly.\n" +
                        "- Try different keywords.\n" +
                        "- Try more general keywords.\n";
        noResultsEmbedBuilder
                //.setTitle(":warning: Your search '" + args + "' did not match any pages!")
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
        if (query.length() > MessageEmbed.TITLE_MAX_LENGTH) {
            return tooLongEmbed;
        } else {
            long queryStart = System.nanoTime();
            PageResult[] queryResults = pageIndex.query(query);
            float queryTime = Duration.ofNanos(System.nanoTime() - queryStart).toMillis();
            if (queryResults.length < 1) {
                return warnNoResults(query);
            } else {
                return getResults(queryResults, queryTime, query);
            }
        }
    }

    private MessageEmbed getResults(PageResult[] queryResults, float queryTime, String args) {
        EmbedBuilder builder = new EmbedBuilder(defaultEmbedBuilder);
        //String footerText = "Requested by " + channel.getAuthor().getAsTag() + " - Found in " + Math.round(queryTime) + "ms";
        //builder.setFooter(footerText, channel.getAuthor().getAvatarUrl());
        String footerText = "Found in " + Math.round(queryTime) + "ms";
        builder.setFooter(footerText);
        PageResult[] displayResults = clampResults(queryResults, MAX_RESULTS);
        // Add cursory info to embed
        if (queryResults.length == 1) {
            builder.setTitle(":mag: Found 1 relevant page");
        } else {
            builder.setTitle(":mag: Found " + queryResults.length + " relevant pages");
            if (queryResults.length > MAX_RESULTS) {
                builder.appendDescription("*Displaying the first " + displayResults.length + " most relevant results:*");
            } else {
                builder.appendDescription("\u200B"); // Blank character
            }
        }
        addResultsToEmbed(builder, displayResults);
        return builder.build();
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
