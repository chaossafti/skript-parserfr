package de.safti.skparser.syntaxes.event;

import de.safti.skparser.runtime.TriggerContext;
import de.safti.skparser.syntaxes.ElementMetadata;

public interface EventCheckHandler {
    boolean check(TriggerContext context, ElementMetadata metadata);
}
