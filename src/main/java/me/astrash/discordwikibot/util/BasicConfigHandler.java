package me.astrash.discordwikibot.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

public class BasicConfigHandler {

    public static void setup(Properties config, String configPath) throws IOException {

        File configFile = new File(configPath);

        // Make template config if one doesn't exist
        if (!configFile.isFile()) {
            System.out.println("Fill out the information in " + configPath + " and restart the jar!");

            FileWriter configWriter = new FileWriter(configFile);
            configWriter.write(
                    "wikiURI = URI\n" +
                    "botToken = TOKEN");
            configWriter.close();
            System.exit(0);
        }

        FileInputStream configStream = new FileInputStream(configFile);
        config.load(configStream);
        configStream.close();
    }
}
