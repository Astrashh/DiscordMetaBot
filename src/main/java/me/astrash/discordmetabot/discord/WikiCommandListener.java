package me.astrash.discordmetabot.discord;

import me.astrash.discordmetabot.index.page.PageIndex;
import me.astrash.discordmetabot.index.page.PageResult;
import me.astrash.discordmetabot.util.CommandUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.time.Duration;
import java.util.Arrays;

public class WikiCommandListener extends ListenerAdapter {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(WikiCommandListener.class);

    PageIndex pageIndex;

    String commandPrefix = ".";
    String[] commandAliases = {"wiki", "w"};
    String embedImageURL = "https://cdn.discordapp.com/icons/715448651786485780/b913e035edaf9515a922e3e79fdb351a.webp";

    int embedColor = 0x2F3136;
    int embedColorWarning = 0xF8C300;
    int embedColorError = 0xDD0000;

    int maxResults = 3;

    public WikiCommandListener(PageIndex indexer) {
        this.pageIndex = indexer;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;
        StringBuilder command = new StringBuilder(event.getMessage().getContentRaw());
        if (CommandUtil.consumePrefix(command, commandPrefix)) {
            for (String prefix : commandAliases) {
                if (CommandUtil.consumePrefix(command, prefix)) {
                    if (command.toString().startsWith(" ")) {
                        searchWiki(command.substring(1), event);
                        return;
                    } else {
                        System.out.println("Print how to search");
                    }
                }
            }
        }
    }

    private void searchWiki(String input, MessageReceivedEvent event) {

        logger.info("==================================");
        logger.info("Searching for: " + input);

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setThumbnail(embedImageURL);

        if (input.length() > MessageEmbed.TITLE_MAX_LENGTH) {
            embedBuilder
                    .setTitle(":exclamation: Your requested search is too long")
                    .setColor(embedColorError)
                    .setDescription("Please use a shorter query!");
        } else {
            embedBuilder.setColor(embedColor);

            long queryStart = System.nanoTime();
            PageResult[] queryResults = pageIndex.query(input);
            float queryTime = Duration.ofNanos(System.nanoTime() - queryStart).toMillis();

            // If there are no results
            if (queryResults.length < 1) {

                String suggestions =
                    "- Make sure that all words are spelled correctly.\n" +
                    "- Try different keywords.\n" +
                    "- Try more general keywords.\n";

                embedBuilder
                    .setTitle(":warning: Your search '" + input + "' did not match any pages!")
                    .addField("Suggestions", suggestions, false)
                    .setColor(embedColorWarning);
            }
            // If results are found
            else {
                String footerText =
                        "Requested by " + event.getAuthor().getAsTag() +
                                " - Found in " + Math.round(queryTime) + "ms";

                embedBuilder.setFooter(footerText, event.getAuthor().getAvatarUrl());

                // Clamp amount of results
                PageResult[] displayResults = Arrays.copyOfRange(queryResults, 0, Math.min(maxResults, queryResults.length));

                // Add cursory info to embed
                if (queryResults.length == 1) {
                    embedBuilder.setTitle(":mag: Found 1 relevant page");
                }
                else {
                    embedBuilder.setTitle(":mag: Found " + queryResults.length + " relevant pages");
                    if (queryResults.length > maxResults) {
                        embedBuilder.appendDescription("*Displaying the first " + displayResults.length + " most relevant results:*");
                    } else {
                        embedBuilder.appendDescription("\u200B"); // Blank character
                    }
                }

                // Add results to embed
                Arrays.stream(displayResults).forEachOrdered(result -> embedBuilder.addField(
                        clampString(":page_facing_up: " + result.getHeading(), MessageEmbed.TITLE_MAX_LENGTH),
                        clampString(result.getDescription(), MessageEmbed.VALUE_MAX_LENGTH),
                        false
                ));
            }
        }

        MessageEmbed msg = embedBuilder.build();
        event.getChannel().sendMessage(msg).queue();
    }

    private String clampString(String input, int limit) {
        String output;
        if (input.length() > limit) {
            output = input.substring(input.length() - 3) + "...";
        }
        else {
            output = input;
        }
        return output;
    }
}
