package me.astrash.discordmetabot.config;

import com.dfsek.tectonic.annotations.Value;
import com.dfsek.tectonic.config.ConfigTemplate;

public class Config implements ConfigTemplate {

    @Value("wiki.uri")
    private String wikiURI;

    @Value("wiki.pull-branch")
    private String pullBranch;

    @Value("discord.token")
    private String discordBotToken;

    public String getWikiURI() {
        return wikiURI;
    }

    public String getPullBranch() {
        return pullBranch;
    }

    public String getDiscordBotToken() {
        return discordBotToken;
    }
}
