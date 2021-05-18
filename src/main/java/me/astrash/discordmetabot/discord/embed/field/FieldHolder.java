package me.astrash.discordmetabot.discord.embed.field;

import java.util.ArrayList;
import java.util.List;

public class FieldHolder {
    private final List<Field> fields;
    private static FieldHolder emptyFieldHolder = new FieldHolder(new ArrayList<>());

    public FieldHolder(List<Field> fields) {
        this.fields = fields;
    }

    public static FieldHolder getEmptyHolder() {
        return emptyFieldHolder;
    }

    public List<Field> getFields() {
        return fields;
    }
}
