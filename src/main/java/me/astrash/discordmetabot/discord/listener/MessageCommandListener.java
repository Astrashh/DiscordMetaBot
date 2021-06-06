package me.astrash.discordmetabot.discord.listener;

import me.astrash.discordmetabot.command.CommandHolder;
import me.astrash.discordmetabot.command.EventCommand;
import me.astrash.discordmetabot.command.StringCommandArgs;
import me.astrash.discordmetabot.config.ConfigHandler;
import me.astrash.discordmetabot.util.CommandUtil;
import me.astrash.discordmetabot.util.discord.MessageUtil;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class MessageCommandListener extends ListenerAdapter {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(MessageCommandListener.class);

    private final ConfigHandler configHandler;
    private final CommandHolder<EventCommand<MessageReceivedEvent>> commandHolder;

    public MessageCommandListener(ConfigHandler configHandler, CommandHolder<EventCommand<MessageReceivedEvent>> commandHolder) {
        this.commandHolder = commandHolder;
        this.configHandler = configHandler;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;
        StringBuilder input = new StringBuilder(event.getMessage().getContentRaw());
        if (CommandUtil.consumePrefix(input, configHandler.getConfig().getCommandPrefix())) {
            String commandName = CommandUtil.consumeFirst(input);
            EventCommand<MessageReceivedEvent> command = commandHolder.fetch(commandName);
            if (!command.equals(EventCommand.NULL_COMMAND)) {
                command.run(new StringCommandArgs(input.toString()), event);
            } else {
                MessageUtil.sendError("Unknown command '" + commandName + "'", event.getChannel());
            }
        }
    }

    /*
    @Override
    public void onMessageUpdate(MessageUpdateEvent event) {
        if (tagEdits.containsKey(event.getMessageId())) {
            String previewMessageId = tagEdits.get(event.getMessageId());
            String codeBlock = MessageUtil.getFirstCodeBlock(event.getMessage().getContentRaw());
            try {
                MessageEmbed embed = tagIndex.testEmbed(codeBlock, "test");
                if (embed != null && embed.isSendable()) {
                    event.getChannel().editMessageById(previewMessageId, embed).queue();
                }
                else {
                    event.getChannel().editMessageById(previewMessageId, MarkdownUtil.codeblock("diff", "- Invalid tag config!")).queue();
                }
            } catch (ConfigException e) {
                event.getChannel().editMessageById(previewMessageId, MarkdownUtil.codeblock("diff", "- " + e.toString())).queue();
            }
        }
    }

     */
}
