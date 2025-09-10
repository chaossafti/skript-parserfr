package de.safti.skparser.syntaxes.effects;

import de.safti.skparser.runtime.TriggerContext;
import de.safti.skparser.syntaxes.ElementMetadata;

public interface EffectExecuteHandler {

    void execute(TriggerContext context, ElementMetadata metadata);

}
