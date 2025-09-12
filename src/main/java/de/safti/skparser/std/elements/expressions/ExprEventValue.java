package de.safti.skparser.std.elements.expressions;

import de.safti.skparser.SkriptParser;
import de.safti.skparser.logging.SkriptLogger;
import de.safti.skparser.logging.errors.IllegalUsageError;
import de.safti.skparser.pattern.match.MatchContext;
import de.safti.skparser.runtime.TriggerContext;
import de.safti.skparser.syntaxes.ElementMetadata;
import de.safti.skparser.syntaxes.event.EventInfo;
import de.safti.skparser.syntaxes.event.EventStructureElement;
import de.safti.skparser.syntaxes.event.EventValue;
import de.safti.skparser.syntaxes.expression.ExpressionHandler;
import de.safti.skparser.syntaxes.parsed.StructureElement;
import de.safti.skparser.syntaxes.parsed.SyntaxElement;

import java.util.Optional;

public class ExprEventValue implements ExpressionHandler<Object> {
    public static final String PATTERN = "[the] event-<.*>";

    @Override
    public Class<Object> typeClass(SkriptParser parser, String raw) {
        String eventValueName = raw.substring(raw.indexOf('-') + 1);

        EventValue<?, ?> eventValue = parser.getSyntaxManager().getEventValue(eventValueName);
        if(eventValue == null) throw new IllegalArgumentException("Cannot parse event value: " + raw  + ", extracted name: " + eventValueName);

        // ts is safe
        //noinspection unchecked
        return (Class<Object>) eventValue.getTypeClass();
    }


    @Override
    public Object[] evaluateAll(TriggerContext context, ElementMetadata metadata) {
        EventValue<?, ?> eventValue = metadata.get("event-value", EventValue.class);
        if (eventValue == null) {
            return new Object[0];
        }

        Class<?> contextClass = eventValue.getContextClass();
        if (!contextClass.isInstance(context)) {
            // context does not match -> nothing to return
            return new Object[0];
        }

        // helper method is required due to generics
        return evaluateForEventValue(eventValue, context);
    }

    @SuppressWarnings("unchecked")
    private <C extends TriggerContext, T> Object[] evaluateForEventValue(EventValue<T, C> eventValue, TriggerContext context) {
        C castedContext = (C) context; // safe due to isInstance check above
        T value = eventValue.getValue(castedContext);

        if (value == null) {
            return new Object[0];
        }

        // returned multiple values -> return as array
        if (value.getClass().isArray()) {
            return (Object[]) value;
        }

        // returned single value -> create new array
        return new Object[] {value};
    }

    @Override
    public boolean init(MatchContext context, SkriptLogger logger, SyntaxElement element, ElementMetadata metadata) {
        // get and validate event value name
        String eventValueName = context.getMatchAt(0);
        if(eventValueName == null || eventValueName.isEmpty()) {
            logger.alert(new IllegalUsageError(context.getInput(), "is not supported", "any event value supported by this event!"));
            return false;
        }

        // find event
        StructureElement root = element.findRoot();
        if(!(root instanceof EventStructureElement event)) {
            logger.alert(new IllegalUsageError(context.getInput(), "in a structure", "in an event"));
            return false;
        }


        // get the event value by name
        EventInfo info = event.getInfo();
        Optional<EventValue<?, ?>> eventValue = info.eventValue(eventValueName);

        // validate event value presence
        if(eventValue.isEmpty()) {
            logger.alert(new IllegalUsageError(context.getInput(), "is not supported by this event!", "use a valid event value!"));
            return false;
        }


        metadata.set("event-value", eventValue.get());
        return true;
    }
}
