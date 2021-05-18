package me.astrash.discordmetabot.index;

import me.astrash.discordmetabot.discord.embed.Embed;
import me.astrash.discordmetabot.index.lucene.LuceneIndexer;
import me.astrash.discordmetabot.util.FileUtil;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

/*
 * Builds up an index of information in the form of discord embeds
 */
public class InfoIndex {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(LuceneIndexer.class);

    // IDs of error template embed(s)
    List<String> errorTemplates = new ArrayList<>(Arrays.asList(
            "error",
            "base"
    ));
    // Actual embeds of said IDs
    List<Embed> errorEmbedTemplates = new ArrayList<>();

    // Built JDA embeds
    Map<String, MessageEmbed> messageEmbeds = new HashMap<>();

    public InfoIndex(String dataPath) throws IOException {
        Map<String, Embed> embeds = new HashMap<>();

        // Make embeds from YAML files
        FileUtil.getFilesWithExtensions(dataPath, new String[]{".yml",".yaml"}).forEach(path -> {
            try {
                Embed embed = Embed.fromYaml(path);
                logger.info("Loading embed '" + embed.getId() + "'");
                embeds.put(embed.getId().toLowerCase(), embed);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        });

        errorTemplates.forEach(template -> errorEmbedTemplates.add(embeds.get(template)));

        // Add embeds as templates of other embeds as specified in YAML
        embeds.values().forEach(embed -> {
            List<Embed> embedTemplates = new ArrayList<>();

            if (embed.getTemplates() != null) {
                embed.getTemplates().forEach(templateString -> {
                    if (embeds.get(templateString) != null) embedTemplates.add(embeds.get(templateString));
                });
                embed.setEmbedTemplates(embedTemplates);
            }
        });

        // Build embeds
        embeds.values().forEach(embed -> {
            if (!embed.isTemplate()) {
                messageEmbeds.put(embed.getId(), embed.build());
            }
        });
    }

    public MessageEmbed query(String input) {

        // TODO - Better search functionality
        //        Allow for more flexibility in searching for info snippets
        String parsedInput = input.toLowerCase();

        MessageEmbed embed = messageEmbeds.get(parsedInput);
        if (embed == null) {
            Embed errorMessage = new Embed();
            errorMessage.setId("errorMessage");
            errorMessage.setEmbedTemplates(errorEmbedTemplates);
            errorMessage.setDescription("Could not find information for '" + parsedInput + "'");
            return errorMessage.build();
        }
        return embed;
    }
}
