package de.safti.skparser.std.elements.expressions;

import de.safti.skparser.logging.SkriptLogger;
import de.safti.skparser.pattern.PatternCompiler;
import de.safti.skparser.pattern.SyntaxPattern;
import de.safti.skparser.pattern.match.MatchContext;
import de.safti.skparser.runtime.TriggerContext;
import de.safti.skparser.syntaxes.ElementMetadata;
import de.safti.skparser.syntaxes.expression.ExpressionHandler;

public class ExprStringLiteral implements ExpressionHandler<String> {
    // "(?:[^"\\]|\\.)*"
    // matches "anything in quotation marks \" may be escaped using a backslash"
    // the enclosing quotation marks are included.
    private static final String STRING_LITERAL_REGEX = "\"(?:[^\"\\\\]|\\\\.)*\"";

    public static final SyntaxPattern PATTERN = PatternCompiler.compile("<%s>".formatted(STRING_LITERAL_REGEX));



    @Override
    public boolean init(MatchContext context, SkriptLogger logger, ElementMetadata metadata) {
        String string = context.getMatchAt(0);
        string = string.substring(1, string.length() - 1);

        metadata.set("CONTENT", string);
        return true;
    }

    @Override
    public String evaluate(TriggerContext context, ElementMetadata metadata) {
        return metadata.getString("CONTENT");
    }

    @Override
    public String[] evaluateAll(TriggerContext context, ElementMetadata metadata) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Class<String> typeClass() {
        return String.class;
    }
}
