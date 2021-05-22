package me.astrash.discordmetabot.discord;

import me.astrash.discordmetabot.index.InfoIndex;
import me.astrash.discordmetabot.index.page.PageIndex;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

import javax.security.auth.login.LoginException;

public class BotHandler {

    private JDA bot;

    public BotHandler(String token, PageIndex pageIndex, InfoIndex infoIndex) throws LoginException {
        JDABuilder builder = JDABuilder.createDefault(token);
        this.bot = builder
                .addEventListeners(new MessageListener(pageIndex, infoIndex))
                .build();
    }

    public JDA getBot() {
        return bot;
    }
}
