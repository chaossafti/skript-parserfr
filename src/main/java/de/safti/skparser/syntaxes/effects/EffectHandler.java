package de.safti.skparser.syntaxes.effects;

import de.safti.skparser.runtime.TriggerContext;
import de.safti.skparser.syntaxes.ElementMetadata;
import de.safti.skparser.syntaxes.SyntaxHandler;

public interface EffectHandler extends SyntaxHandler {

    void execute(TriggerContext context, ElementMetadata metadata);

}
