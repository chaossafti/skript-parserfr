package de.safti.skparser.syntaxes.structure;

import de.safti.skparser.runtime.TriggerContext;
import de.safti.skparser.syntaxes.ElementMetadata;
import de.safti.skparser.syntaxes.SyntaxHandler;
import de.safti.skparser.syntaxes.SyntaxInfo;
import de.safti.skparser.syntaxes.effects.EffectInfo;
import de.safti.skparser.syntaxes.parsed.StructureElement;
import de.safti.skparser.syntaxes.parsed.SyntaxElement;

import java.util.List;

public interface StructureHandler extends SyntaxHandler {
    default void walk(List<SyntaxElement> body, TriggerContext context, ElementMetadata metadata) {
        for (SyntaxElement element : body) {
            SyntaxInfo info = element.getInfo();
            if(info instanceof EffectInfo effectInfo) {
                effectInfo.handler().execute(context, element.getMetadata());
            } else if(element instanceof StructureElement subStructure) {
                subStructure.walk(context);
            } else {
                throw new IllegalStateException("Could not find ways to execute: " + element.getClass().getName() + ". Content: " + element.getRaw());
            }
        }

    }




}
