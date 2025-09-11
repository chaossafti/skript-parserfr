package de.safti.skparser.events;

import de.safti.skparser.runtime.TriggerContext;
import de.safti.skparser.syntaxes.event.EventStructureElement;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SkriptEventManager {
    private final Map<Class<?>, List<EventStructureElement>> listeners = new ConcurrentHashMap<>();
    private final Set<EventStructureElement> globalListeners = ConcurrentHashMap.newKeySet();

    /**
     * Registers the given element to be called only when the specified event type is triggered.
     *
     * @param eventType The type of event this element should listen for.
     * @param element   The element to walk through when the event occurs.
     * @param <T>       The type of {@link TriggerContext} associated with the event.
     */
    public <T extends TriggerContext> void register(Class<T> eventType, EventStructureElement element) {
        listeners.computeIfAbsent(eventType, c -> new ArrayList<>()).add(element);
    }

    /**
     * Walks through the given element whenever any event is called.
     *
     * @param element The element to walk through.
     */
    public void registerGlobal(EventStructureElement element) {
        globalListeners.add(element);
    }

    /**
     * Unregisters the given element from:
     * <ul>
     *   <li>All event-specific listener lists</li>
     *   <li>The global listeners set</li>
     * </ul>
     *
     * @param element The element to unregister.
     */
    public void unregister(EventStructureElement element) {
        // remove from all event-specific lists
        for (Iterator<Map.Entry<Class<?>, List<EventStructureElement>>> it = listeners.entrySet().iterator(); it.hasNext();) {
            Map.Entry<Class<?>, List<EventStructureElement>> entry = it.next();
            entry.getValue().remove(element);
            if (entry.getValue().isEmpty()) {
                it.remove(); // clean up empty lists
            }
        }

        // remove from global set
        globalListeners.remove(element);
    }

    /**
     * Unregisters the given element from the global listeners set.
     *
     * @param element The element to remove.
     */
    public void unregisterGlobal(EventStructureElement element) {
        globalListeners.remove(element);
    }

    /**
     * Calls all listeners registered for the given trigger context,
     * as well as all global listeners.
     *
     * @param context The context to dispatch to listeners.
     */
    public <T extends TriggerContext> void call(T context) {
        // type-specific listeners
        List<EventStructureElement> list = listeners.get(context.getClass());
        if (list != null) {
            for (EventStructureElement element : list) {
                element.walk(context);
            }
        }

        // global listeners
        for (EventStructureElement element : globalListeners) {
            element.walk(context);
        }
    }
}