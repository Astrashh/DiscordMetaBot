package me.astrash.discordmetabot.index;

import com.dfsek.tectonic.exception.ConfigException;
import com.dfsek.tectonic.loading.ConfigLoader;
import me.astrash.discordmetabot.config.ConfigHandler;
import me.astrash.discordmetabot.discord.embed.Embed;
import me.astrash.discordmetabot.discord.embed.field.FieldHolder;
import me.astrash.discordmetabot.discord.embed.field.FieldHolderLoader;
import me.astrash.discordmetabot.util.FileUtil;
import net.dv8tion.jda.api.entities.MessageEmbed;

import javax.annotation.Nullable;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class InfoIndex {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(ConfigHandler.class);

    private Map<String, MessageEmbed> embeds = new HashMap<>();

    public InfoIndex(String infoPath) throws IOException {

        ConfigLoader loader = new ConfigLoader();
        loader.registerLoader(FieldHolder.class, new FieldHolderLoader());

        FileUtil.getFilesWithExtensions(infoPath, new String[]{".yml",".yaml"}).forEach(path -> {
            Embed embed = new Embed();
            try {
                FileInputStream yaml = new FileInputStream(path);
                loader.load(embed, yaml);
                yaml.close();
                embeds.put(embed.getId(), embed.build());
            } catch (ConfigException | IOException e) {
                logger.error("Could not load info file: ", e);
            }
        });
    }

    @Nullable
    public MessageEmbed query(String input) {
        return embeds.get(input);
    }
}
