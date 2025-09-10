package de.safti.skparser.pattern.nodes;

import de.safti.skparser.logging.SkriptLogger;
import de.safti.skparser.pattern.PatternNode;
import de.safti.skparser.pattern.TypeMatchNode;
import de.safti.skparser.pattern.match.MatchContext;
import de.safti.skparser.pattern.match.SyntaxMatchResult;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class EitherNode implements PatternNode {
    private final List<PatternNode> options;

    public EitherNode(List<PatternNode> options) {
        this.options = options;
    }

    @Override
    public SyntaxMatchResult matches(String input, MatchContext ctx) {
        for (PatternNode option : options) {
            SyntaxMatchResult res = option.matches(input, ctx.copy());
            if (res.isSuccess()) return res;
        }
        return SyntaxMatchResult.failure();
    }

    @Override
    public int matchAndCollectTypes(@NotNull String input, int startIndex,
                                    @NotNull List<TypeMatchNode> matches,
                                    @NotNull MatchContext context, SkriptLogger logger) {
        for (PatternNode option : options) {
            int nextIndex = option.matchAndCollectTypes(input, startIndex, matches, context, logger);
            if (nextIndex != -1) {
                return nextIndex; // return first successful option
            }
        }
        return -1; // no options matched
    }
    @Override
    public String toString() {
        return "(either: " + options + ")";
    }

    @NotNull
    public List<PatternNode> getOptions() {
        return options;
    }
}
