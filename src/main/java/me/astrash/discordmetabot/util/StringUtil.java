package me.astrash.discordmetabot.util;

public final class StringUtil {
    private StringUtil(){}

    public static String clampString(String input, int limit, String clampString) {
        String output;
        if (input.length() > limit) {
            output = input.substring(0, limit - clampString.length()) + clampString;
        }
        else {
            output = input;
        }
        return output;
    }

    public static String clampString(String input, int limit) {
        return clampString(input, limit, "...");
    }
}
