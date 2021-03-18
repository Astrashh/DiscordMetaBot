package me.astrash.discordwikibot;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.List;

public class MessageListener extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;
        // Only respond to non-bots

        Message message  = event.getMessage();
        String content = message.getContentRaw();

        if (content.equals("test")) {
            MessageChannel channel = event.getChannel();

            EmbedBuilder embedBuilder = new EmbedBuilder()
                    .setTitle("Title")
                    .setColor(0xF8C300)
                    .appendDescription("description");

            MessageEmbed msg = embedBuilder.build();
            channel.sendMessage(msg).queue();
        }
    }
}
