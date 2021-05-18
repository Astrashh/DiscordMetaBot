package me.astrash.discordmetabot.discord.embed.field;

import net.dv8tion.jda.api.entities.MessageEmbed;

public class Field {
    private String name;
    private String value;
    private boolean inline;

    public MessageEmbed.Field build() {
        return new MessageEmbed.Field(name, value, inline);
    }

    public Field setName(String name) {
        this.name = name;
        return this;
    }

    public Field setValue(String value) {
        this.value = value;
        return this;
    }

    public Field setInline(boolean inline) {
        this.inline = inline;
        return this;
    }
}
