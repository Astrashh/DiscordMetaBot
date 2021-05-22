package me.astrash.discordmetabot.discord.embed;

import com.dfsek.tectonic.annotations.Abstractable;
import com.dfsek.tectonic.annotations.Default;
import com.dfsek.tectonic.annotations.Value;
import com.dfsek.tectonic.config.ConfigTemplate;
import me.astrash.discordmetabot.discord.embed.field.Field;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Embed implements ConfigTemplate {

    public MessageEmbed build() {
        EmbedBuilder builder = new EmbedBuilder();
        builder
                .setAuthor(authorName, authorUrl, authorIconUrl)
                .setTitle(titleText, titleUrl)
                .setDescription(description)
                .setFooter(footerText, footerIconUrl)
                .setImage(image)
                .setThumbnail(thumbnail)
                .setColor(color);

        fields.forEach(field -> builder.addField(field.build()));
        return builder.build();
    }

    @Value("id")
    private String id;

    @Value("aliases")
    @Default
    private List<String> aliases = new ArrayList<>();

    @Value("author.name")
    @Abstractable
    @Default
    private String authorName = null;

    @Value("author.url")
    @Abstractable
    @Default
    private String authorUrl = null;

    @Value("author.icon-url")
    @Abstractable
    @Default
    private String authorIconUrl = null;

    @Value("title.text")
    @Abstractable
    @Default
    private String titleText = null;

    @Value("title.url")
    @Abstractable
    @Default
    private String titleUrl = null;

    @Value("description")
    @Abstractable
    @Default
    private String description = null;

    @Value("fields")
    @Abstractable
    @Default
    private List<Field> fields = Collections.emptyList();

    @Value("footer.text")
    @Abstractable
    @Default
    private String footerText = null;

    @Value("footer.icon-url")
    @Abstractable
    @Default
    private String footerIconUrl = null;

    @Value("image")
    @Abstractable
    @Default
    private String image = null;

    @Value("thumbnail")
    @Abstractable
    @Default
    private String thumbnail = null;

    @Value("color")
    @Abstractable
    @Default
    private int color = Role.DEFAULT_COLOR_RAW;

    public String getId() {
        return id;
    }
}
