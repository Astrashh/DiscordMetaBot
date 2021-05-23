package me.astrash.discordmetabot.util;

public final class CommandUtil {
    private CommandUtil(){}

    public static boolean consumePrefix(StringBuilder string, String prefix) {
        if (string.toString().startsWith(prefix)) {
            string.delete(0, prefix.length());
            return true;
        }
        return false;
    }
}
