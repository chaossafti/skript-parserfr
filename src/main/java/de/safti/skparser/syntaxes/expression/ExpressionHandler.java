package de.safti.skparser.syntaxes.expression;

import de.safti.skparser.SkriptParser;
import de.safti.skparser.runtime.TriggerContext;
import de.safti.skparser.syntaxes.ElementMetadata;
import de.safti.skparser.syntaxes.SyntaxHandler;
import de.safti.skparser.types.Type;

public interface ExpressionHandler<T> extends SyntaxHandler {

    /**
     * @return The returned type class of this expression
     */
    // TODO: replace raw with some kind of api
    Class<T> typeClass(SkriptParser parser, String raw);

    default Type type(SkriptParser parser, String raw) {
        return parser.getTypeManager().getTypeByClass(typeClass(parser, raw));
    }

    default T evaluate(TriggerContext context, ElementMetadata metadata) {
        T[] ts = evaluateAll(context, metadata);
        if(ts != null && ts.length > 0)
            return ts[ts.length - 1];
        return null;
    }

    T[] evaluateAll(TriggerContext context, ElementMetadata metadata);

}
