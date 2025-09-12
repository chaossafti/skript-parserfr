package de.safti.skparser.pattern.nodes;

import de.safti.skparser.logging.SkriptLogger;
import de.safti.skparser.pattern.PatternNode;
import de.safti.skparser.pattern.TypeMatchNode;
import de.safti.skparser.pattern.match.MatchContext;
import de.safti.skparser.pattern.match.SyntaxMatchResult;
import de.safti.skparser.syntaxes.parsed.SyntaxElement;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SequenceNode implements PatternNode {
    private final List<PatternNode> nodes;

    public SequenceNode(List<PatternNode> nodes) {
        this.nodes = nodes;
    }

    @Override
    public SyntaxMatchResult matches(String input, MatchContext ctx) {
        String remaining = input;
        MatchContext working = ctx.copy();

        for (PatternNode node : nodes) {
            SyntaxMatchResult res = node.matches(remaining.trim(), working);
            if (!res.isSuccess()) return SyntaxMatchResult.failure();
            remaining = res.getRemaining().strip();
            working = res.getContext();
        }

        return SyntaxMatchResult.success(remaining, working);
    }

    public <N> List<N> collectNodes(Class<N> nodeType, List<N> list) {
        for (PatternNode node : nodes) {
            if(node.getClass().isAssignableFrom(nodeType)) {
                list.add(nodeType.cast(node));
            }

            if(node instanceof SequenceNode sequenceNode) {
                sequenceNode.collectNodes(nodeType, list);
            }
        }

        return list;
    }

    @Override
    public int matchAndCollectTypes(@NotNull String input, int startIndex,
                                    @NotNull List<TypeMatchNode> matches,
                                    @NotNull MatchContext context, SkriptLogger logger, SyntaxElement argumentHolder) {
        int currentIndex = startIndex;
        for (PatternNode child : nodes) {
            int nextIndex = child.matchAndCollectTypes(input, currentIndex, matches, context, logger, argumentHolder);
            if (nextIndex == -2) continue;
            if(nextIndex == -1) return -1;
            currentIndex = nextIndex;
        }
        return currentIndex;
    }

    @Override
    public String toString() {
        return "(seq " + nodes + ")";
    }

    @NotNull
    public List<PatternNode> getNodes() {
        return nodes;
    }
}
