package me.astrash.discordwikibot;

import me.astrash.discordwikibot.index.LuceneIndexer;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.lucene.queryparser.classic.ParseException;

import java.io.IOException;
import java.util.List;

public class MessageListener extends ListenerAdapter {

    LuceneIndexer indexer;
    String baseCommand = ".t wiki ";

    public MessageListener(LuceneIndexer indexer) {
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

            try {
                indexer.query(input);
            } catch (IOException | ParseException e) {
                e.printStackTrace();
            }

            EmbedBuilder embedBuilder = new EmbedBuilder()
                    .setTitle("Title")
                    .setColor(0xF8C300)
                    .appendDescription("description");

            MessageEmbed msg = embedBuilder.build();
            channel.sendMessage(msg).queue();
        }
    }
}
