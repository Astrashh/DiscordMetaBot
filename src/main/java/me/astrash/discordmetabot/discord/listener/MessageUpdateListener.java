package me.astrash.discordmetabot.discord.listener;

import me.astrash.discordmetabot.command.CommandHolder;
import me.astrash.discordmetabot.command.EventCommand;
import me.astrash.discordmetabot.command.StringCommandArgs;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class MessageUpdateListener extends ListenerAdapter {

    private CommandHolder<EventCommand<MessageUpdateEvent>> commandHolder;

    public MessageUpdateListener(CommandHolder<EventCommand<MessageUpdateEvent>> commandHolder) {
        this.commandHolder = commandHolder;
    }

    @Override
    public void onMessageUpdate(MessageUpdateEvent event) {
        if (!event.getAuthor().isBot()) {
            commandHolder.fetch("editupdate").run(new StringCommandArgs(event.getMessage().getContentRaw()), event);
        }
    }
}
