package de.safti.skparser.syntaxes.event;

import de.safti.skparser.events.SkriptEventManager;
import de.safti.skparser.logging.SkriptLogger;
import de.safti.skparser.logging.errors.IllegalUsageError;
import de.safti.skparser.pattern.match.MatchContext;
import de.safti.skparser.runtime.TriggerContext;
import de.safti.skparser.syntaxes.ElementMetadata;
import de.safti.skparser.syntaxes.SyntaxInfo;
import de.safti.skparser.syntaxes.parsed.SyntaxElement;
import de.safti.skparser.syntaxes.structure.StructureHandler;
import org.jetbrains.annotations.ApiStatus;

import java.util.Set;

public interface EventHandler extends StructureHandler {

    boolean check(TriggerContext context, ElementMetadata metadata);

    /**
     * @param isRoot dummy argument
     */
    boolean init(MatchContext context, SkriptLogger logger, SyntaxElement metadata, boolean isRoot);

    /**
     * This method assures events are parsed at root level and registered to the event manager.
     * Extend {@link #init(MatchContext, SkriptLogger, SyntaxElement, boolean)} instead.
     */
    @Override
    @ApiStatus.NonExtendable
    default boolean init(MatchContext context, SkriptLogger logger, SyntaxElement element, ElementMetadata metadata) {
        SyntaxInfo info = element.getInfo();

        // check that the syntax info is an EventInfo, otherwise fail fast
        if(!(info instanceof EventInfo eventInfo)) {
            throw new UnsupportedOperationException("Illegal info: " + info.getClass() + ", expected event info.");
        }

        // events must only be declared at root level
        if(!context.isRoot()) {
            logger.alert(new IllegalUsageError(context.getInput(), "outside of the root", "at root level"));
            return false;
        }

        SkriptEventManager eventManager = context.getParser().getEventManager();

        // call the extended init method
        if(init(context, logger, element, context.isRoot())) {
            Set<Class<? extends TriggerContext>> supported = eventInfo.supportedContext();

            // if no specific context is given, register as a global event
            if(supported == null) {
                eventManager.registerGlobal((EventStructureElement) element);
            } else {
                // otherwise, register for each supported context
                for (Class<? extends TriggerContext> triggerContextClass : supported) {
                    eventManager.register(triggerContextClass, (EventStructureElement) element);
                }
            }
            return true;
        }

        return false;
    }

    /**
     * Unregisters the given element from the event manager.
     * Called when the syntax element is unloaded.
     */
    @Override
    default void unload(SyntaxElement element) {
        StructureHandler.super.unload(element);
        SyntaxInfo info = element.getInfo();

        // check that the syntax info is an EventInfo, otherwise fail fast
        if(!(info instanceof EventInfo eventInfo)) {
            throw new UnsupportedOperationException("Illegal info: " + info.getClass() + ", expected event info.");
        }

        SkriptEventManager eventManager = element.getParser().getEventManager();
        Set<Class<? extends TriggerContext>> supported = eventInfo.supportedContext();

        // unregister events based on supported context
        if(supported == null) {
            eventManager.unregisterGlobal((EventStructureElement) element);
        } else {
            eventManager.unregister((EventStructureElement) element);
        }
    }
}
