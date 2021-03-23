package me.astrash.discordmetabot.discord;

import me.astrash.discordmetabot.index.Indexer;
import me.astrash.discordmetabot.index.QueryResult;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.time.Duration;
import java.util.Arrays;

public class MessageListener extends ListenerAdapter {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(MessageListener.class);

    Indexer indexer;
    String baseCommand = ".t ";
    String wikiSubCommand = "wiki ";
    String infoSubCommand = "info ";
    String embedImageURL = "https://cdn.discordapp.com/icons/715448651786485780/b913e035edaf9515a922e3e79fdb351a.webp";
    int embedColor = 0x2F3136;
    int embedColorWarning = 0xF8C300;
    int embedColorError = 0xDD0000;
    int maxResults = 3;

    public MessageListener(Indexer indexer) { this.indexer = indexer; }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;

        Message message = event.getMessage();
        String content = message.getContentRaw();

        // Lazy command parsing
        if (content.startsWith(baseCommand)) {
            String subContent = content.substring(baseCommand.length());
            if (subContent.startsWith(wikiSubCommand)) {
                String input = subContent.substring(wikiSubCommand.length());
                searchWiki(input, event);
            } else if (subContent.startsWith(infoSubCommand)) {
                String input = subContent.substring(infoSubCommand.length()).split(" ")[0];
                displayInfo(input, event);
            }
        }
    }

    private void displayInfo(String page, MessageReceivedEvent event) {
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle(page)
                .setColor(embedColor)
                .setDescription("Some information");

        // Send embed to channel
        MessageEmbed msg = embedBuilder.build();
        event.getChannel().sendMessage(msg).queue();
    }

    private void searchWiki(String input, MessageReceivedEvent event) {

        logger.info("==================================");
        logger.info("Searching for: " + input);

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setThumbnail(embedImageURL);

        String embedTitle = "Displaying pages related to '" + input + "':";

        // Set embed to error message if the input is too long to be displayed
        if (embedTitle.length() > MessageEmbed.TITLE_MAX_LENGTH) {
            embedBuilder
                    .setTitle("Your requested search is too long")
                    .setColor(embedColorError)
                    .setDescription("Please use a shorter query!");
        } else {
            embedBuilder
                    .setTitle(embedTitle)
                    .setColor(embedColor);

            long queryStart = System.nanoTime();
            QueryResult[] queryResults = indexer.query(input);
            float queryTime = Duration.ofNanos(System.nanoTime() - queryStart).toMillis();

            // If there are no results
            if (queryResults.length < 1) {

                String suggestions =
                    "- Make sure that all words are spelled correctly.\n" +
                    "- Try different keywords.\n" +
                    "- Try more general keywords.\n";

                embedBuilder
                    .setTitle("Your search '" + input + "' did not match any pages!")
                    .addField("Suggestions", suggestions, false);
            }
            // If results are found
            else {
                String footerText =
                        "Requested by " + event.getAuthor().getAsTag() +
                                " - Found in " + Math.round(queryTime) + "ms";

                embedBuilder.setFooter(footerText, event.getAuthor().getAvatarUrl());

                // Clamp amount of results
                QueryResult[] displayResults = Arrays.copyOfRange(queryResults, 0, Math.min(maxResults, queryResults.length));

                // Add cursory info to embed
                embedBuilder.setTitle("Found " + queryResults.length + " relevant pages");
                if (queryResults.length > maxResults) {
                    embedBuilder.appendDescription("*Displaying the first " + maxResults + " most relevant results:*");
                }

                // Add results to embed
                Arrays.stream(displayResults).forEachOrdered(result -> {
                    embedBuilder.addField(
                            clampString(result.getHeading(), MessageEmbed.TITLE_MAX_LENGTH),
                            clampString(result.getDescription(), MessageEmbed.VALUE_MAX_LENGTH),
                            false
                    );
                });
            }
        }

        // Send embed to channel
        MessageEmbed msg = embedBuilder.build();
        event.getChannel().sendMessage(msg).queue();
    }

    // Convenience method
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
