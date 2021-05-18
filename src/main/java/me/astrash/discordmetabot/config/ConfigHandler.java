package me.astrash.discordmetabot.config;

import com.dfsek.tectonic.exception.ConfigException;
import com.dfsek.tectonic.loading.ConfigLoader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ConfigHandler {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(ConfigHandler.class);

    public static Config setup(String configPath) throws IOException, ConfigException {

        Files.createDirectories(Paths.get(configPath));
        File configFile = new File(configPath + "/config.yml");

        // Make template config if one doesn't exist
        if (!configFile.isFile()) {
            logger.info("Fill out the information in " + configPath + " and restart the jar!");

            FileWriter configWriter = new FileWriter(configFile);
            configWriter.write(
                    "wiki:\n" +
                    "  uri: \n" +
                    "  pull-branch: \n" +
                    "discord:\n" +
                    "  token: ");
            configWriter.close();

            System.exit(0);
        }

        FileInputStream configStream = new FileInputStream(configFile);
        Config config = new Config();
        ConfigLoader loader = new ConfigLoader();
        loader.load(config, configStream);
        configStream.close();
        return config;
    }
}
