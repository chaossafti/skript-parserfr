package de.safti.skparser.pattern.nodes;

import de.safti.skparser.SkriptParser;
import de.safti.skparser.logging.SkriptLogger;
import de.safti.skparser.pattern.PatternNode;
import de.safti.skparser.pattern.TypeMatchNode;
import de.safti.skparser.pattern.match.MatchContext;
import de.safti.skparser.pattern.match.SyntaxMatchResult;
import de.safti.skparser.syntaxes.SyntaxManager;
import de.safti.skparser.syntaxes.expression.ExpressionHandler;
import de.safti.skparser.syntaxes.expression.ExpressionInfo;
import de.safti.skparser.syntaxes.parsed.SyntaxElement;
import de.safti.skparser.types.Type;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TypeNode implements PatternNode {
    private final String expectedTypeName;

    public TypeNode(String typeName) {
        this.expectedTypeName = typeName;
    }

    @Override
    public SyntaxMatchResult matches(String input, MatchContext ctx) {
        SkriptParser parser = ctx.getParser();

        // parse info and assure it's an expression
        SyntaxManager.ElementParseResult parseResult = parser.getSyntaxManager().parseElement(input, parser);
        if(parseResult == null) {
            return SyntaxMatchResult.failure();
        }

        if(!(parseResult.info() instanceof ExpressionInfo expressionInfo)) {
            return SyntaxMatchResult.failure();
        }

        // extract non-null type
        ExpressionHandler<?> handler = expressionInfo.handler();
        // FIXME: print expects string, but we offer it an object
        // 2 changes:
        //  - make print take object
        //  - make this parse safe somehow
        Type type = handler.type(parser, ctx.getInput());
        // sanity check
        if(type == null) return SyntaxMatchResult.failure();

        if(type.codeName().equals(expectedTypeName)) {
            return SyntaxMatchResult.success("", ctx);
        }

        return SyntaxMatchResult.failure();
    }

    @Override
    public int matchAndCollectTypes(@NotNull String input,
                                    int startIndex,
                                    @NotNull List<TypeMatchNode> matches,
                                    @NotNull MatchContext context, SkriptLogger logger,
                                    SyntaxElement argumentHolder) {

        // parse the argument as an expression
        SyntaxElement element = context.getParser().parseElement(input.substring(startIndex), logger, argumentHolder.getParent());
        if (element != null) {
            int endIndex = startIndex + element.getRaw().length();
            matches.add(new TypeMatchNode(element, this, startIndex, endIndex));
            return endIndex;
        }
        return -1;
    }


    @Override
    public String toString() {
        return "%"+ expectedTypeName +"%";
    }

    public String getExpectedTypeName() {
        return expectedTypeName;
    }
}
