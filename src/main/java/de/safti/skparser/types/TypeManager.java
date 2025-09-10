package de.safti.skparser.types;

import de.safti.skparser.SkriptParser;
import de.safti.skparser.pattern.SyntaxPattern;
import org.jetbrains.annotations.Unmodifiable;

import java.util.HashMap;
import java.util.Map;

public class TypeManager {
    private Map<Class<?>, Type> types = new HashMap<>();

    public TypeManager() {
    }

    public <T> void register(Class<T> clazz, String codeName, String pattern) {
        types.put(clazz, new Type(codeName, SyntaxPattern.compile(pattern), clazz));
    }

    @Unmodifiable
    public Map<Class<?>, Type> getTypes() {
        return Map.copyOf(types);
    }

    public Type getTypeByMatch(String raw, SkriptParser parser) {
        for (Type value : types.values()) {
            if(value.pattern().matches(raw, parser)) {
                return value;
            }
        }

        return null;
    }

    public <T> Type getTypeByClass(Class<T> tClass) {
        return types.get(tClass);
    }
}
