package de.safti.skparser.pattern.nodes;

import de.safti.skparser.logging.SkriptLogger;
import de.safti.skparser.pattern.PatternNode;
import de.safti.skparser.pattern.TypeMatchNode;
import de.safti.skparser.pattern.match.MatchContext;
import de.safti.skparser.pattern.match.SyntaxMatchResult;
import de.safti.skparser.syntaxes.parsed.SyntaxElement;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class OptionalNode implements PatternNode {
    private final PatternNode inner;

    public OptionalNode(PatternNode inner) {
        this.inner = inner;
    }

    @Override
    public SyntaxMatchResult matches(String input, MatchContext context) {
        SyntaxMatchResult res = inner.matches(input, context.copy());
        return res.isSuccess() ? res : SyntaxMatchResult.success(input, context); // skip if fail
    }

    @Override
    public int matchAndCollectTypes(@NotNull String input, int startIndex,
                                    @NotNull List<TypeMatchNode> matches,
                                    @NotNull MatchContext context, SkriptLogger logger, SyntaxElement argumentHolder) {
        int nextIndex = inner.matchAndCollectTypes(input, startIndex, matches, context, logger, argumentHolder);
        if (nextIndex == -1) {
            // skip if no match
            return startIndex;
        }
        return nextIndex;
    }

    @Override
    public String toString() {
        return "(optional " + inner + ")";
    }

    public PatternNode getInner() {
        return inner;
    }
}
