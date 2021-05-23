package me.astrash.discordmetabot.discord;

import me.astrash.discordmetabot.index.TagIndex;
import me.astrash.discordmetabot.util.CommandUtil;
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
        StringBuilder command = new StringBuilder(event.getMessage().getContentRaw());
        if (CommandUtil.consumePrefix(command, commandPrefix)) {
            for (String prefix : commandAliases) {
                if (CommandUtil.consumePrefix(command, prefix)) {
                    if (command.toString().startsWith(" ")) {
                        String input = command.substring(1).split(" ")[0];
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
