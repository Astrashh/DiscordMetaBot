package me.astrash.discordmetabot.command;

public interface CommandArgs {

    CommandArgs EMPTY = () -> null;

    String getRaw();
}
