package de.safti.skparser.pattern.nodes;

import de.safti.skparser.logging.SkriptLogger;
import de.safti.skparser.pattern.PatternNode;
import de.safti.skparser.pattern.TypeMatchNode;
import de.safti.skparser.pattern.match.MatchContext;
import de.safti.skparser.pattern.match.SyntaxMatchResult;
import de.safti.skparser.syntaxes.parsed.SyntaxElement;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class LiteralNode implements PatternNode {
    private final String literal;

    public LiteralNode(String literal) {
        this.literal = literal;
    }

    @Override
    public SyntaxMatchResult matches(String input, MatchContext ctx) {
        if (input.startsWith(literal)) {
            return SyntaxMatchResult.success(input.substring(literal.length()), ctx);
        }
        return SyntaxMatchResult.failure();
    }

    @Override
    public int matchAndCollectTypes(@NotNull String input, int startIndex,
                                    @NotNull List<TypeMatchNode> matches,
                                    @NotNull MatchContext context, SkriptLogger logger, SyntaxElement argumentHolder) {
        if (input.startsWith(literal, startIndex)) {
            return startIndex + literal.length();
        }
        return -1;
    }

    @Override
    public String toString() {
        return "(literal: " + literal + ")";
    }

    @NotNull
    public String getLiteral() {
        return literal;
    }
}