package me.astrash.discordmetabot.config;

import com.dfsek.tectonic.exception.ConfigException;
import com.dfsek.tectonic.loading.ConfigLoader;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigHandler {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(ConfigHandler.class);

    private final Path path;
    private Config config = new Config();

    public ConfigHandler(Path path) throws IOException {
        this.path = path;
        Files.createDirectories(path.getParent());
        if (!Files.exists(path)) {
            dumpDefaultConfig();
        }
        loadConfig();
    }

    public void loadConfig() throws IOException {
        FileInputStream configStream = new FileInputStream(path.toFile());
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

    private void dumpDefaultConfig() throws IOException {
        FileWriter writer = new FileWriter(path.toFile());
        writer.write(
                "wiki:\n" +
                "  uri: \n" +
                "  pull-branch: \n" +
                "discord:\n" +
                "  token: ");
        writer.close();

        logger.info("Fill out the information in " + path + " and restart the jar!");
        System.exit(0);
    }
}
