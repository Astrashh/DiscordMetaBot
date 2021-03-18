package me.astrash.discordwikibot;

import me.astrash.discordwikibot.util.BasicConfigHandler;
import net.dv8tion.jda.api.JDABuilder;

import javax.security.auth.login.LoginException;
import java.io.*;
import java.util.Properties;

public class DiscordWikiBot {
    public static void main(String[] args) {

        // Simple temporary config reader
        Properties config = null;
        try {
            config = BasicConfigHandler.setup("./resources/config.properties");
        } catch (IOException e) {
            System.out.println("Failed to load config!");
            e.printStackTrace();
            System.exit(-1);
        }

        // Setting up discord bot
        try {
            setupBot(config.getProperty("discordBotToken"));
        } catch (LoginException e) {
            System.out.print("Failed to set up Discord bot via JDA!");
            e.printStackTrace();
        }
    }

    private static void setupBot(String token) throws LoginException {
        JDABuilder builder = JDABuilder.createDefault(token);
        builder.addEventListeners(new MessageListener());
        builder.build();
    }
}