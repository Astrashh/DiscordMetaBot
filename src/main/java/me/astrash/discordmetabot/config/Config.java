package me.astrash.discordmetabot.config;

import com.dfsek.tectonic.annotations.Value;
import com.dfsek.tectonic.config.ConfigTemplate;

public class Config implements ConfigTemplate {

    @Value("wiki.uri")
    private String wikiURI;

    @Value("wiki.pull-branch")
    private String pullBranch;

    @Value("wiki.search-embed-image")
    private String searchEmbedImage;

    @Value("wiki.clone-automatically")
    private boolean cloneWiki;

    @Value("discord.token")
    private String discordBotToken;

    @Value("discord.command-prefix")
    private String commandPrefix;

    @Value("discord.test-guild-name")
    private String testGuildName;

    public String getWikiURI() {
        return wikiURI;
    }

    public String getPullBranch() {
        return pullBranch;
    }

    public String getWikiSearchEmbedImage() {
        return searchEmbedImage;
    }

    public String getDiscordBotToken() {
        return discordBotToken;
    }

    public String getCommandPrefix() {
        return commandPrefix;
    }

    public boolean cloneWiki() {
        return cloneWiki;
    }

    public String getTestGuildName() {
        return testGuildName;
    }
}
