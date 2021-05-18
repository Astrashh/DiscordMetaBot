package me.astrash.discordmetabot.discord.embed;

import com.dfsek.tectonic.annotations.Default;
import com.dfsek.tectonic.annotations.Value;
import com.dfsek.tectonic.config.ConfigTemplate;
import me.astrash.discordmetabot.discord.embed.field.FieldHolder;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;

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
        fields.getFields().forEach(field -> builder.addField(field.build()));
        return builder.build();
    }

    @Value("id")
    private String id;

    @Value("author.name")
    @Default
    private String authorName = null;

    @Value("author.url")
    @Default
    private String authorUrl = null;

    @Value("author.icon-url")
    @Default
    private String authorIconUrl = null;

    @Value("title.text")
    @Default
    private String titleText = null;

    @Value("title.url")
    @Default
    private String titleUrl = null;

    @Value("description")
    @Default
    private String description = null;

    @Value("fields")
    @Default
    private FieldHolder fields = FieldHolder.getEmptyHolder();

    @Value("footer.text")
    @Default
    private String footerText = null;

    @Value("footer.icon-url")
    @Default
    private String footerIconUrl = null;

    @Value("image")
    @Default
    private String image = null;

    @Value("thumbnail")
    @Default
    private String thumbnail = null;

    @Value("color")
    @Default
    private int color = Role.DEFAULT_COLOR_RAW;

    public String getId() {
        return id;
    }
}
