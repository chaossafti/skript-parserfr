package de.safti.skparser.syntaxes.event;

import de.safti.skparser.SkriptParser;
import de.safti.skparser.logging.SkriptLogger;
import de.safti.skparser.runtime.TriggerContext;
import de.safti.skparser.syntaxes.parsed.StructureElement;
import de.safti.skparser.syntaxes.parsed.SyntaxElement;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

public class EventStructureElement extends StructureElement {

    public EventStructureElement(String raw, EventInfo info, List<SyntaxElement> body, SkriptParser parser, SkriptLogger logger) {
        super(raw, info, body, parser, logger);
    }

    public boolean accepts(TriggerContext context) {
        Set<Class<? extends TriggerContext>> supportedContexts = getInfo().supportedContext();
        // true -> all contexts are supported
        if(supportedContexts == null) return true;

        return supportedContexts.contains(context.getClass());
    }

    @Override
    public void walk(TriggerContext context) {
        if(!accepts(context)) {
            throw new IllegalArgumentException(context.getClass().getCanonicalName() + " cannot be used for " + getRaw() + "! " + " accepted types are: " + getInfo().supportedContext());
        }

        super.walk(context);
    }

    @Override
    public @NotNull EventInfo getInfo() {
        return (EventInfo) super.getInfo();
    }
}
