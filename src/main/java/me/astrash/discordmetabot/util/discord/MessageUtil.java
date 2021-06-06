package me.astrash.discordmetabot.util.discord;

import me.astrash.discordmetabot.util.StringUtil;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import net.dv8tion.jda.api.utils.MarkdownUtil;

import javax.annotation.Nullable;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class MessageUtil {
    private MessageUtil() {}

    public static List<String> getCodeBlocks(String input) {
        Pattern pattern = Pattern.compile("```([\\S]*\\n)?([\\s\\S]+?)```");
        Matcher matcher = pattern.matcher(input);
        List<String> matches = new ArrayList<>();
        while (matcher.find()) {
            matches.add(matcher.group(2));
        }
        return matches;
    }

    @Nullable
    public static String getFirstCodeBlock(String input) {
        List<String> matches = getCodeBlocks(input);
        if (!matches.isEmpty()) return matches.get(0);
        else return null;
    }

    public static void sendError(String message, MessageChannel channel) {
        channel.sendMessage(MarkdownUtil.codeblock("diff", "- " + message)).queue();
    }

    public static void sendError(String message, MessageChannel channel, Exception e, boolean full) {
        MessageAction msg = channel.sendMessage(message + "\n");
        if (full) {
            StringWriter sw = new StringWriter(); PrintWriter pw = new PrintWriter(sw); e.printStackTrace(pw);
            msg.append(MarkdownUtil.codeblock("diff", "- " + StringUtil.clampString(sw.toString(), 500).replace("\n", "\n- ")));
        } else {
            msg.append(MarkdownUtil.codeblock("diff", "- " + e.toString()));
        }
        msg.queue();
    }
}
