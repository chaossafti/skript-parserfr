package de.safti.skparser.syntaxes.event;

import de.safti.skparser.pattern.SyntaxPattern;
import de.safti.skparser.runtime.TriggerContext;
import de.safti.skparser.syntaxes.structure.StructureInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.Set;

public final class EventInfo extends StructureInfo {

    /**
     * Creates an event for the given context class.
     * More handled context classes may be added later.
     *
     * @param contextClass The context class.
     * @param <C> The type of context.
     * @return A new EventBuilder.
     */
    public static <C extends TriggerContext> EventBuilder<C> eventBuilder(Class<C> contextClass) {
        return new EventBuilder<>();
    }

    @Nullable
    private final Set<Class<? extends TriggerContext>> supportedContext;
    private final Set<EventValue<?, ?>> eventValues;

    /**
     * @param supportedContext The context supported. If null, all contexts are supported.
     */
    public EventInfo(int priority, SyntaxPattern pattern,
                     EventHandler handler, @Nullable Set<Class<? extends TriggerContext>> supportedContext,
                     @NotNull Set<EventValue<?, ?>> eventValues

    ) {
        super(pattern, priority, handler);
        this.supportedContext = supportedContext;
        this.eventValues = eventValues;
    }

    @Override
    public EventHandler handler() {
        return (EventHandler) super.handler();
    }

    @NotNull
    public Set<EventValue<?, ?>> eventValues() {
        return eventValues;
    }

    public Optional<EventValue<?,?>> eventValue(String name) {
        return eventValues.stream()
                .filter(eventValue -> eventValue.getName().equals(name))
                .findFirst();
    }

    /**
     * @return The context supported. If null, all contexts are supported.
     */
    public @Nullable Set<Class<? extends TriggerContext>> supportedContext() {
        return supportedContext;
    }

}
