package me.astrash.discordmetabot.config;

import com.dfsek.tectonic.exception.ConfigException;
import com.dfsek.tectonic.loading.ConfigLoader;
import me.astrash.discordmetabot.util.FileUtil;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigHandler {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(ConfigHandler.class);

    private final Path path;
    private final Path configPath;
    private final Config config = new Config();

    public ConfigHandler(Path path) throws IOException {
        this.path = path;
        configPath = path.resolve("config.yml");
        reload();
    }

    public void reload() throws IOException {
        if (!Files.exists(configPath)) {
            FileUtil.dumpResources(this.getClass(), path, "config.yml");
            logger.info("Fill out the information in " + configPath + " and restart the jar!");
            System.exit(0);
        }

        FileInputStream configStream = new FileInputStream(configPath.toFile());
        ConfigLoader loader = new ConfigLoader();
        try {
            loader.load(config, configStream);
        } catch (ConfigException e) {
            e.printStackTrace();
        }
        configStream.close();
    }

    public Config getConfig() {
        return config;
    }
}
