package de.safti.skparser.pattern;

import de.safti.skparser.logging.SkriptLogger;
import de.safti.skparser.pattern.match.MatchContext;
import de.safti.skparser.pattern.match.SyntaxMatchResult;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface PatternNode {
    int MATCH_FAILURE = -1;

    SyntaxMatchResult matches(String input, MatchContext ctx);


    /**
     * Default implementation for extracting type matches.
     * Returns -1 to indicate no match; override in nodes that can match.
     *
     * @param input      the input string
     * @param startIndex the current index in the input
     * @param matches    list to collect matched TypeNodes
     * @param context    match context
     * @param logger     The logger to log things to
     * @return the index after this node matched, or -1 if the match failed
     */
    int matchAndCollectTypes(@NotNull String input, int startIndex,
                             @NotNull List<TypeMatchNode> matches,
                             @NotNull MatchContext context, SkriptLogger logger);

}