package me.astrash.discordmetabot.discord.embed.field;

import com.dfsek.tectonic.loading.ConfigLoader;
import com.dfsek.tectonic.loading.TypeLoader;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FieldHolderLoader implements TypeLoader<FieldHolder> {

    @Override
    public FieldHolder load(Type t, Object o, ConfigLoader loader) {
        List<Map<Object, Object>> rawFields = (List<Map<Object, Object>>) o;
        List<Field> fields = new ArrayList<>();
        rawFields.forEach(field -> fields.add(new Field()
            .setName((String) field.get("name"))
            .setValue((String) field.get("value"))
            .setInline((boolean) field.get("inline"))
        ));
        return new FieldHolder(fields);
    }
}
