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
    int maxResults = 3;

    public MessageListener(Indexer indexer) {
        this.indexer = indexer;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;
        // Only respond to non-bots

        Message message  = event.getMessage();
        String content = message.getContentRaw();

        if (content.startsWith(baseCommand)) {
            MessageChannel channel = event.getChannel();

            String input = content.substring(baseCommand.length());

            System.out.println("========================================================");
            System.out.println("Searching for: " + input);

            EmbedBuilder embedBuilder = new EmbedBuilder()
                    .setTitle("Displaying pages related to '" + input + "':")
                    .setColor(0xF8C300);

            QueryResult[] queryResults = indexer.query(input);

            // If there are no results
            if (queryResults.length < 1) {
                embedBuilder.setTitle("Your search '" + input + "' did not match any pages!")
                    .addField("Suggestions",
                        "- Make sure that all words are spelled correctly.\n" +
                        "- Try different keywords.\n" +
                        "- Try more general keywords.\n",
                        false
                    );
            }
            // If results are found
            else {
                // Clamp amount of results
                QueryResult[] displayResults = Arrays.copyOfRange(queryResults, 0, Math.min(maxResults, queryResults.length));

                embedBuilder.setTitle("Found " + queryResults.length + " relevant pages");
                if (queryResults.length > maxResults) {
                    embedBuilder.appendDescription("*Displaying the first " + maxResults + " most relevant results:*");
                }

                // Add results to embed
                Arrays.stream(displayResults).forEachOrdered(result -> {
                    embedBuilder.addField(result.getHeading(), result.getDescription(), false);
                });
            }

            MessageEmbed msg = embedBuilder.build();
            channel.sendMessage(msg).queue();
        }
    }
}
