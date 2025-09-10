package de.safti.skparser.syntaxes;

import de.safti.skparser.logging.SkriptLogger;
import de.safti.skparser.pattern.match.MatchContext;

public interface SyntaxHandler {

    boolean init(MatchContext context, SkriptLogger logger, ElementMetadata metadata);

    // TODO: debug to string

}
