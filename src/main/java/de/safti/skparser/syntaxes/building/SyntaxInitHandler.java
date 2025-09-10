package de.safti.skparser.syntaxes.building;

import de.safti.skparser.logging.SkriptLogger;
import de.safti.skparser.pattern.match.MatchContext;
import de.safti.skparser.syntaxes.ElementMetadata;

public interface SyntaxInitHandler {
    boolean init(MatchContext context, SkriptLogger logger, ElementMetadata metadata);
}
