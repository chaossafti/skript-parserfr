package de.safti.skparser.pattern;

import de.safti.skparser.SkriptParser;
import de.safti.skparser.logging.SkriptLogger;
import de.safti.skparser.pattern.match.MatchContext;
import de.safti.skparser.pattern.match.SyntaxMatchResult;
import de.safti.skparser.pattern.nodes.SequenceNode;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SyntaxPattern {
    private final String raw;
    private final PatternNode root;

    SyntaxPattern(String raw, PatternNode root) {
        this.raw = raw;
        this.root = root;
    }

    public static SyntaxPattern compile(String raw) {
        return PatternCompiler.compile(raw);
    }

    public String getRaw() {
        return raw;
    }

    public PatternNode getRoot() {
        return root;
    }

    public boolean matches(String input, SkriptParser parser) {
        input = input.strip(); // remove leading/trailing spaces
        SyntaxMatchResult result = root.matches(input, new MatchContext(parser));
        // ignore leftover whitespace
        return result.isSuccess() && result.getRemaining().isBlank();
    }

    public SyntaxMatchResult matchDetailed(String input, SkriptParser parser) {
        return root.matches(input, new MatchContext(parser));
    }

    public List<TypeMatchNode> extractTypeMatches(@NotNull String input, @NotNull SkriptParser parser, SkriptLogger logger) {
        input = input.strip(); // remove leading/trailing spaces
        List<TypeMatchNode> matches = new ArrayList<>();
        MatchContext context = new MatchContext(parser);

        // Walk the root node recursively
        root.matchAndCollectTypes(input, 0, matches, context, logger);

        return matches;
    }



    @Override
    public String toString() {
        return "SyntaxPattern{raw='" + raw + "', root=" + root + "}";
    }

    @NotNull
    public <N extends PatternNode> List<N> nodes(Class<N> nodeType) {
        List<N> result = new ArrayList<>();

        if(root instanceof SequenceNode sequenceNode) {
            return sequenceNode.collectNodes(nodeType, result);
        }

        if(root.getClass().isAssignableFrom(nodeType)) {
            //noinspection unchecked
            return List.of((N) root);
        }

        return result;
    }


}