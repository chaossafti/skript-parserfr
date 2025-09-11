package de.safti.skparser.syntaxes.event;

import de.safti.skparser.pattern.SyntaxPattern;
import de.safti.skparser.runtime.TriggerContext;
import de.safti.skparser.syntaxes.structure.StructureInfo;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public final class EventInfo extends StructureInfo {

    public static EventBuilder eventBuilder() {
        return new EventBuilder();
    }

    @Nullable
    private final Set<Class<? extends TriggerContext>> supportedContext;

    /**
     * @param supportedContext The context supported. If null, all contexts are supported.
     */
    public EventInfo(int priority, SyntaxPattern pattern,
                     EventHandler handler, @Nullable Set<Class<? extends TriggerContext>> supportedContext) {
        super(pattern, priority, handler);
        this.supportedContext = supportedContext;
    }

    @Override
    public EventHandler handler() {
        return (EventHandler) super.handler();
    }

    /**
     * @return The context supported. If null, all contexts are supported.
     */
    public @Nullable Set<Class<? extends TriggerContext>> supportedContext() {
        return supportedContext;
    }

}
