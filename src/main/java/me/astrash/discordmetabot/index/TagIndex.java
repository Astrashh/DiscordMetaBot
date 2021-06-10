package me.astrash.discordmetabot.index;

import com.dfsek.tectonic.abstraction.AbstractConfigLoader;
import com.dfsek.tectonic.config.Configuration;
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
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class TagIndex implements DiscreteIndex<String, MessageEmbed> {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(ConfigHandler.class);

    private List<Configuration> configs;
    private Map<String, MessageEmbed> embeds = new HashMap<>();

    private final Path tagPath;
    private final AbstractConfigLoader loader;
    private final List<String> reservedIDs = Arrays.asList("template");
    private final String reservedPrefix = "_";

    public TagIndex(Path tagPath) throws IOException, ConfigException {
        this.tagPath = tagPath;
        this.loader = new AbstractConfigLoader();
        loader.registerLoader(Field.class, new FieldLoader());
        FileUtil.dumpResources(this.getClass(), tagPath, "_template.yml");
        reloadTags();
    }

    public void reloadTags() throws IOException, ConfigException {
        configs = new ArrayList<>();
        Files.createDirectories(tagPath);
        FileUtil.getFilesWithExtensions(tagPath, new String[]{".yml",".yaml"}).forEach(path -> {
            if (!FileUtil.getBaseName(path).startsWith(reservedPrefix)) {
                try {
                    InputStream stream = new FileInputStream(path);
                    String name = FileUtil.removeExtension(String.valueOf(tagPath.relativize(Paths.get(path))));
                    Configuration config = new Configuration(stream, name);
                    configs.add(config);
                } catch (IOException e) {
                    logger.error("Could not load tag file: ", e);
                }
            }
        });
        embeds = loadEmbeds(configs);
    }

    public Map<String, MessageEmbed> getEmbeds() {
        return Collections.unmodifiableMap(embeds);
    }

    private Map<String, MessageEmbed> loadEmbeds(List<Configuration> configs) throws ConfigException {
        return loader.loadConfigs(configs, Embed::new).stream()
            //.filter(embed -> reservedIDs.contains(embed.getId())) // Don't load embed if id is reserved
            .collect(Collectors.toMap(Embed::getId, Embed::build));
    }

    @Nullable
    public MessageEmbed testEmbed(String yaml, String id) throws ConfigException {
        List<Configuration> protoConfigs = new ArrayList<>(this.configs); // Duplicate configs
        protoConfigs.add(new Configuration(yaml));                        // Add new config to set
        Map<String, MessageEmbed> protoEmbeds = loadEmbeds(protoConfigs); // Load configs
        return protoEmbeds.get(id);
    }

    @Nullable
    public MessageEmbed query(String input) {
        return embeds.get(input);
    }

    @Override
    public Map<String, MessageEmbed> getAll() {
        return new HashMap<>(embeds);
    }

    public Path getTagPath() {
        return tagPath;
    }
}
