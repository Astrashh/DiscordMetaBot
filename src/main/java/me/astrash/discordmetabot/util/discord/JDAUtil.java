package me.astrash.discordmetabot.util.discord;

import me.astrash.discordmetabot.discord.MessageListener;
import me.astrash.discordmetabot.index.InfoIndex;
import me.astrash.discordmetabot.index.PageIndex;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

import javax.security.auth.login.LoginException;

public final class JDAUtil {
    private JDAUtil(){}

    public static void setupBot(String token, PageIndex pageIndex, InfoIndex infoIndex) throws LoginException {

        JDABuilder builder = JDABuilder.createDefault(token);
        JDA bot = builder
                .addEventListeners(new MessageListener(pageIndex, infoIndex))
                .build();
    }
}
