package me.astrash.discordmetabot.index;

import com.dfsek.tectonic.abstraction.AbstractConfigLoader;
import com.dfsek.tectonic.exception.ConfigException;
import me.astrash.discordmetabot.config.ConfigHandler;
import me.astrash.discordmetabot.discord.embed.Embed;
import me.astrash.discordmetabot.discord.embed.field.Field;
import me.astrash.discordmetabot.discord.embed.field.FieldLoader;
import me.astrash.discordmetabot.util.FileUtil;
import net.dv8tion.jda.api.entities.MessageEmbed;

import javax.annotation.Nullable;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TagIndex implements Index<String, MessageEmbed> {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(ConfigHandler.class);

    private Map<String, MessageEmbed> embeds = new HashMap<>();

    public TagIndex(Path tagPath) throws IOException {

        AbstractConfigLoader loader = new AbstractConfigLoader();
        loader.registerLoader(Field.class, new FieldLoader());

        List<InputStream> streams = new ArrayList<>();

        Files.createDirectories(tagPath);
        FileUtil.getFilesWithExtensions(tagPath, new String[]{".yml",".yaml"}).forEach(path -> {
            try {
                streams.add(new FileInputStream(path));
            } catch (IOException e) {
                logger.error("Could not load tag file: ", e);
            }
        });

        try {
            embeds = loader.load(streams, Embed::new).stream().collect(Collectors.toMap(Embed::getId, Embed::build));
        } catch (ConfigException e) {
            e.printStackTrace();
        }
    }

    @Nullable
    public MessageEmbed query(String input) {
        return embeds.get(input);
    }
}
