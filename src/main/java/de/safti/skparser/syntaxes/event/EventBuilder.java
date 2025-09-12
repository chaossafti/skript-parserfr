package de.safti.skparser.syntaxes.event;

import de.safti.skparser.bootstrap.SyntaxLoader;
import de.safti.skparser.logging.SkriptLogger;
import de.safti.skparser.pattern.PatternCompiler;
import de.safti.skparser.pattern.SyntaxPattern;
import de.safti.skparser.pattern.match.MatchContext;
import de.safti.skparser.runtime.TriggerContext;
import de.safti.skparser.syntaxes.ElementMetadata;
import de.safti.skparser.syntaxes.building.SyntaxInitHandler;
import de.safti.skparser.syntaxes.parsed.SyntaxElement;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Function;

public class EventBuilder<C extends TriggerContext> {

    private int priority;
    private SyntaxPattern pattern;
    private EventHandler eventHandler;
    private final Set<Class<? extends TriggerContext>> contextTypes = new HashSet<>();
    private final Set<EventValue<?, C>> eventValues = new HashSet<>();

    private SyntaxInitHandler initHandler;
    private EventCheckHandler checkHandler;

    // ------------------------
    // Builder setters
    // ------------------------

    public EventBuilder<C> priority(int priority) {
        this.priority = priority;
        return this;
    }

    public EventBuilder<C> pattern(SyntaxPattern pattern) {
        this.pattern = pattern;
        return this;
    }

    public EventBuilder<C> pattern(String pattern) {
        this.pattern = PatternCompiler.compile(pattern);
        return this;
    }

    public EventBuilder<C> handler(EventHandler handler) {
        this.eventHandler = handler;
        return this;
    }

    public EventBuilder<C> initHandler(SyntaxInitHandler initHandler) {
        this.initHandler = initHandler;
        return this;
    }

    public EventBuilder<C> checkHandler(EventCheckHandler checkHandler) {
        this.checkHandler = checkHandler;
        return this;
    }

    public EventBuilder<C> contextType(Class<C> contextType) {
        this.contextTypes.add(contextType);
        return this;
    }

    public EventBuilder<C> contextTypes(Collection<Class<C>> contextTypes) {
        this.contextTypes.addAll(contextTypes);
        return this;
    }

    public EventBuilder<C> eventValue(EventValue<?, C> eventValue) {
        this.eventValues.add(eventValue);
        return this;
    }

    public <T> EventBuilder<C> eventValue(String name, Class<T> valueClass, Class<C> contextClass, Function<C, T> valueGetter) {
        return eventValue(new EventValue<>(name, valueClass, contextClass, valueGetter));
    }



    // ------------------------
    // Build + Register
    // ------------------------

    public void register(SyntaxLoader loader) {
        EventInfo info = build();
        loader.registerStructure(info);


        // register event values
        for (EventValue<?, C> eventValue : eventValues) {
            loader.registerEventValue(eventValue);
        }
    }

    public EventInfo build() {
        // Ensure init handler always exists
        final SyntaxInitHandler finalInitHandler =
                Objects.requireNonNullElseGet(this.initHandler,
                        () -> (matchContext, logger, element, metadata) -> true);

        // Ensure check handler always exists
        final EventCheckHandler finalCheckHandler =
                Objects.requireNonNullElseGet(this.checkHandler,
                        () -> (context, metadata) -> true);



        EventHandler handler = buildEventHandler(finalCheckHandler, finalInitHandler);
        // this is a safe cast; java generics are simply not good enough
        // to understand that C extends TriggerContext
        //noinspection unchecked
        return new EventInfo(priority, pattern, handler, contextTypes, (Set<EventValue<?,?>>) (Object) eventValues);
    }

    private @NotNull EventHandler buildEventHandler(EventCheckHandler finalCheckHandler, SyntaxInitHandler finalInitHandler) {
        EventHandler handler = this.eventHandler;

        if (handler == null) {
            handler = new EventHandler() {
                @Override
                public boolean check(TriggerContext context, ElementMetadata metadata) {
                    return finalCheckHandler.check(context, metadata);
                }

                @Override
                public boolean init(MatchContext context, SkriptLogger logger, SyntaxElement element, boolean isRoot) {
                    // delegate to provided initHandler
                    return finalInitHandler.init(context, logger, element, element.getMetadata());
                }
            };
        }
        return handler;
    }
}
