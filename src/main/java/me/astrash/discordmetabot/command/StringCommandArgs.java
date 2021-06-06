package me.astrash.discordmetabot.command;

public class StringCommandArgs implements CommandArgs {

    private final String rawArgs;

    public StringCommandArgs(String rawArgs) {
        this.rawArgs = rawArgs;
    }

    @Override
    public String getRaw() {
        return rawArgs;
    }
}
