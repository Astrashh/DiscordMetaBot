package me.astrash.discordmetabot.discord.listener;

import me.astrash.discordmetabot.command.CommandHolder;
import me.astrash.discordmetabot.command.EventCommand;
import me.astrash.discordmetabot.command.StringCommandArgs;
import me.astrash.discordmetabot.config.ConfigHandler;
import me.astrash.discordmetabot.util.CommandUtil;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class MessageReceivedListener extends ListenerAdapter {

    private final ConfigHandler configHandler;
    private final CommandHolder<EventCommand<MessageReceivedEvent>> commandHolder;

    public MessageReceivedListener(ConfigHandler configHandler, CommandHolder<EventCommand<MessageReceivedEvent>> commandHolder) {
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
            if (command != null) command.run(new StringCommandArgs(input.toString()), event);
        }
    }
}
