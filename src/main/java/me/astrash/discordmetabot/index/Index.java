package me.astrash.discordmetabot.index;

public interface Index<T, S> {
    S query(T query);
}
