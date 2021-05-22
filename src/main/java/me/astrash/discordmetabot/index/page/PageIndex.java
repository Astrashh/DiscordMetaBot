package me.astrash.discordmetabot.index.page;

import me.astrash.discordmetabot.index.Index;

public interface PageIndex extends Index<String, PageResult[]> {

    PageResult[] query(String input);

}
