package me.astrash.discordmetabot.discord.embed;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/*
 * A javabean class for building JDA embeds from YAML files
 */
public class Embed {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(Embed.class);

    private String id;
    private List<Embed> embedTemplates = new ArrayList<>();
    private List<String> templates;

    private String authorName;
    private String authorUrl;
    private String authorIconUrl;
    private String titleText;
    private String titleUrl;
    private String description;
    private List<EmbedField> fields = new ArrayList<>();
    private String footerText;
    private String footerIconUrl;
    private String image;
    private String thumbnail;
    private Integer color;
    private boolean template = false;

    public static Embed fromYaml(String file) throws FileNotFoundException {
        Yaml yaml = new Yaml(new Constructor(Embed.class));
        return yaml.load(new InputStreamReader(new FileInputStream(file)));
    }

    public MessageEmbed build() {
        // Apply templates
        embedTemplates.forEach(this::apply);

        EmbedBuilder builder = new EmbedBuilder();

        builder.setTitle(getTitleText(), getTitleUrl());
        builder.setAuthor(getAuthorName(), getAuthorUrl(), getAuthorIconUrl());
        builder.setDescription(description);
        getFields().forEach(field -> {
            builder.addField(field.getName(), field.getValue(), field.isInline());
        });
        builder.setFooter(getFooterText(), getFooterIconUrl());
        builder.setImage(getImage());
        builder.setThumbnail(getThumbnail());
        if (color == null) builder.setColor(Role.DEFAULT_COLOR_RAW);
        else builder.setColor(getColor());

        if(builder.isEmpty()) {
            return null;
        }

        return builder.build();
    }

    public void apply(Embed embed) {
        logger.debug("Applying embed '" + embed.getId() + "' to embed '" + id + "'");

        // Template merging behaviour
        // TODO - Finish merging behaviour

        if (embed.getTitleText() != null && getTitleText() != null) {
            // Use placeholders if both title texts are configured
            String formattedTitle = MessageFormat.format(embed.getTitleText(), getTitleText());
            setTitleText(formattedTitle);
        } else if (embed.getTitleText() != null && getTitleText() == null) {
            // Use the template title text if the embed doesn't have one
            setTitleText(embed.getTitleText());
        }

        if (getTitleUrl() == null) setTitleUrl(embed.getTitleUrl());
        if (color == null) setColor(embed.getColor());
        if (getImage() == null) setImage(embed.getImage());
        if (getThumbnail() == null) setThumbnail(embed.getThumbnail());
    }

    /*
     * Getters & Setters
     */
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Embed> getEmbedTemplates() {
        return embedTemplates;
    }
    public void setEmbedTemplates(List<Embed> embedTemplates) {
        this.embedTemplates = embedTemplates;
    }

    public List<String> getTemplates() {
        return templates;
    }
    public void setTemplates(List<String> templates) {
        this.templates = templates;
    }

    public String getAuthorName() {
        return authorName;
    }
    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getAuthorUrl() {
        return authorUrl;
    }
    public void setAuthorUrl(String authorUrl) {
        this.authorUrl = authorUrl;
    }

    public String getAuthorIconUrl() {
        return authorIconUrl;
    }
    public void setAuthorIconUrl(String authorIconUrl) {
        this.authorIconUrl = authorIconUrl;
    }

    public String getTitleText() {
        return titleText;
    }
    public void setTitleText(String titleText) {
        this.titleText = titleText;
    }

    public String getTitleUrl() {
        return titleUrl;
    }
    public void setTitleUrl(String titleUrl) {
        this.titleUrl = titleUrl;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public List<EmbedField> getFields() {
        return fields;
    }
    public void setFields(List<EmbedField> fields) {
        this.fields = fields;
    }

    public String getFooterText() {
        return footerText;
    }
    public void setFooterText(String footerText) {
        this.footerText = footerText;
    }

    public String getFooterIconUrl() {
        return footerIconUrl;
    }
    public void setFooterIconUrl(String footerIconUrl) {
        this.footerIconUrl = footerIconUrl;
    }

    public String getImage() {
        return image;
    }
    public void setImage(String image) {
        this.image = image;
    }

    public String getThumbnail() {
        return thumbnail;
    }
    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public Integer getColor() {
        return color;
    }
    public void setColor(Integer color) {
        this.color = color;
    }

    public boolean isTemplate() {
        return template;
    }
    public void setTemplate(boolean template) {
        this.template = template;
    }
}
