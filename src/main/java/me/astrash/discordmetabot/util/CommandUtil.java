package me.astrash.discordmetabot.util;

public final class CommandUtil {
    private CommandUtil(){}

    public static String[] split(String input) {
        return input.split("\\s+");
    }

    public static String consumeFirst(StringBuilder input) {
        String first = split(input.toString())[0];
        input.delete(0, first.length() + 1);
        return first;
    }

    public static boolean consumePrefix(StringBuilder string, String prefix) {
        if (string.toString().startsWith(prefix)) {
            string.delete(0, prefix.length());
            return true;
        }
        return false;
    }
}
