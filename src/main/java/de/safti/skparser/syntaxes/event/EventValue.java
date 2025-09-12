package de.safti.skparser.syntaxes.event;

import de.safti.skparser.runtime.TriggerContext;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class EventValue<T, C extends TriggerContext> {
    private final String name;
    private final Class<T> typeClass;
    private final Class<C> contextClass;
    private final Function<C,T> valueGetter;


    public EventValue(@NotNull String name, @NotNull Class<T> typeClass, Class<C> contextClass, @NotNull Function<C, T> valueGetter) {
        this.name = name;
        this.typeClass = typeClass;
        this.contextClass = contextClass;
        this.valueGetter = valueGetter;
    }

    public Class<C> getContextClass() {
        return contextClass;
    }

    public String getName() {
        return name;
    }
    public Class<T> getTypeClass() {
        return typeClass;
    }

    public Function<C,T> getValueGetter() {
        return valueGetter;
    }

    public T getValue(C context) {
        return valueGetter.apply(context);
    }
}
