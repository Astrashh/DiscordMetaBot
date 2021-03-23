package me.astrash.discordmetabot.discord;

import me.astrash.discordmetabot.index.Indexer;
import me.astrash.discordmetabot.index.QueryResult;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Arrays;

public class MessageListener extends ListenerAdapter {

    Indexer indexer;
    String baseCommand = "!wiki ";
    String embedImageURL = "https://cdn.discordapp.com/icons/715448651786485780/b913e035edaf9515a922e3e79fdb351a.webp";
    int embedColor = 0x2F3136;
    int embedColorWarning = 0xF8C300;
    int embedColorError = 0xDD0000;
    int maxResults = 3;

    public MessageListener(Indexer indexer) { this.indexer = indexer; }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        // Only respond to non-bots
        if (event.getAuthor().isBot()) return;

        Message message = event.getMessage();
        String content = message.getContentRaw();

        if (content.startsWith(baseCommand)) {
            MessageChannel channel = event.getChannel();

            String input = content.substring(baseCommand.length());

            System.out.println("========================================================");
            System.out.println("Searching for: " + input);

            EmbedBuilder embedBuilder = new EmbedBuilder();
            //embedBuilder.setImage(embedImageURL);
            embedBuilder.setThumbnail(embedImageURL);

            String embedTitle = "Displaying pages related to '" + input + "':";

            // Set embed to error message if the input is too long to be displayed
            if (embedTitle.length() > MessageEmbed.TITLE_MAX_LENGTH) {
                embedBuilder
                    .setTitle("Your requested search is too long")
                    .setColor(embedColorError)
                    .setDescription("Please use a shorter query!");
            }
            else {
                embedBuilder
                        .setTitle(embedTitle)
                        .setColor(embedColor);

                float queryStart = System.nanoTime();
                QueryResult[] queryResults = indexer.query(input);
                float queryTime = (System.nanoTime() - queryStart) / 1000000;

                embedBuilder.setFooter("Requested by " + event.getAuthor().getAsTag() + " - Found in " + Math.round(queryTime) + "ms", event.getAuthor().getAvatarUrl());

                // If there are no results
                if (queryResults.length < 1) {
                    embedBuilder.setTitle("Your search '" + input + "' did not match any pages!")
                        .addField(
                            "Suggestions",
                            "- Make sure that all words are spelled correctly.\n" +
                            "- Try different keywords.\n" +
                            "- Try more general keywords.\n",
                        false);
                }
                // If results are found
                else {
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
                                clampString(":link: " + result.getDescription(), MessageEmbed.VALUE_MAX_LENGTH),
                                false
                        );
                    });
                }
            }

            // Send embed to channel
            MessageEmbed msg = embedBuilder.build();
            channel.sendMessage(msg).queue();
        }
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
