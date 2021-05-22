package me.astrash.discordmetabot.discord.embed.field;

import com.dfsek.tectonic.loading.ConfigLoader;
import com.dfsek.tectonic.loading.TypeLoader;

import java.lang.reflect.Type;
import java.util.Map;

public class FieldLoader implements TypeLoader<Field> {

    @Override
    public Field load(Type type, Object o, ConfigLoader configLoader) {
        Map<String, Object> rawField = (Map<String, Object>) o;
        return new Field()
            .setName((String) rawField.get("name"))
            .setValue((String) rawField.get("value"))
            .setInline((boolean) rawField.get("inline"));
    }
}
