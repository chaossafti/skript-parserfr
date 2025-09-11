package de.safti.skparser.syntaxes.building;

import de.safti.skparser.logging.SkriptLogger;
import de.safti.skparser.pattern.match.MatchContext;
import de.safti.skparser.syntaxes.ElementMetadata;
import de.safti.skparser.syntaxes.parsed.SyntaxElement;

public interface SyntaxInitHandler {
    /**
     * @param context The match context.
     * @param logger The logger.
     * @param element The SyntaxElement being initialized.
     * @param metadata Quick access to {@link SyntaxElement#getMetadata()}
     * @return true if it initialized successfully, false otherwise. Use the logger to provide useful error messages.
     */
    boolean init(MatchContext context, SkriptLogger logger, SyntaxElement element, ElementMetadata metadata);
}
