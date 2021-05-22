package me.astrash.discordmetabot.index.page;

import me.astrash.discordmetabot.index.Index;

public interface PageIndex<T> extends Index<T, PageResult[]> {

    PageResult[] query(String input);

}
