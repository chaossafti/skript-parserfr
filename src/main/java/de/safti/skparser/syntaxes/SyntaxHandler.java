package de.safti.skparser.syntaxes;

import de.safti.skparser.logging.SkriptLogger;
import de.safti.skparser.pattern.match.MatchContext;
import de.safti.skparser.syntaxes.parsed.SyntaxElement;

public interface SyntaxHandler {

    boolean init(MatchContext context, SkriptLogger logger, SyntaxElement element, ElementMetadata metadata);

    default void unload(SyntaxElement element) {
        // noop by default
    }
}
