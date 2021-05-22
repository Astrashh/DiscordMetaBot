package me.astrash.discordmetabot.discord;

import me.astrash.discordmetabot.index.TagIndex;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class TagCommandListener extends ListenerAdapter {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(TagCommandListener.class);

    TagIndex tagIndex;

    String commandPrefix = ".";
    String[] commandAliases = {"info", "i", "t", "tag"};

    public TagCommandListener(TagIndex tagIndex) {
        this.tagIndex = tagIndex;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;

        Message message = event.getMessage();
        String content = message.getContentRaw();

        if (content.startsWith(commandPrefix)) {
            String subContent = content.substring(commandPrefix.length());
            for (String prefix : commandAliases) {
                if (subContent.startsWith(prefix)) {
                    String commandString = subContent.substring(prefix.length());
                    if (commandString.startsWith(" ")) {
                        String input = commandString.substring(1).split(" ")[0];
                        displayTag(input, event, tagIndex);
                        return;
                    } else {
                        System.out.println("Print how to search");
                    }
                }
            }
        }
    }

    private void displayTag(String page, MessageReceivedEvent event, TagIndex index) {
        MessageEmbed msg = index.query(page);
        if (msg != null) {
            event.getChannel().sendMessage(msg).queue();
        }
    }
}
