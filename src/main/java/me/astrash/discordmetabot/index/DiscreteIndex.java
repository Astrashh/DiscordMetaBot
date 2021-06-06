package me.astrash.discordmetabot.index;

import java.util.Map;

public interface DiscreteIndex<T, S> extends Index<T, S> {
    Map<T, S> getAll();
}
