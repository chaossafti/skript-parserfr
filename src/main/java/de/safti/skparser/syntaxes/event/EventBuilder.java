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

public class EventBuilder {

    private int priority;
    private SyntaxPattern pattern;
    private EventHandler eventHandler;
    private final Set<Class<? extends TriggerContext>> contextTypes = new HashSet<>();

    private SyntaxInitHandler initHandler;
    private EventCheckHandler checkHandler;

    // ------------------------
    // Builder setters
    // ------------------------

    public EventBuilder priority(int priority) {
        this.priority = priority;
        return this;
    }

    public EventBuilder pattern(SyntaxPattern pattern) {
        this.pattern = pattern;
        return this;
    }

    public EventBuilder pattern(String pattern) {
        this.pattern = PatternCompiler.compile(pattern);
        return this;
    }

    public EventBuilder handler(EventHandler handler) {
        this.eventHandler = handler;
        return this;
    }

    public EventBuilder initHandler(SyntaxInitHandler initHandler) {
        this.initHandler = initHandler;
        return this;
    }

    public EventBuilder checkHandler(EventCheckHandler checkHandler) {
        this.checkHandler = checkHandler;
        return this;
    }

    public EventBuilder contextType(Class<? extends TriggerContext> contextType) {
        this.contextTypes.add(contextType);
        return this;
    }

    public EventBuilder contextTypes(Collection<Class<? extends TriggerContext>> contextTypes) {
        this.contextTypes.addAll(contextTypes);
        return this;
    }

    // ------------------------
    // Build + Register
    // ------------------------

    public void register(SyntaxLoader loader) {
        EventInfo info = build();
        loader.registerStructure(info);
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
        return new EventInfo(priority, pattern, handler, contextTypes);
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
