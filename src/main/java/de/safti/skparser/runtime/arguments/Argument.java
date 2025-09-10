package de.safti.skparser.runtime.arguments;

import de.safti.skparser.runtime.TriggerContext;
import de.safti.skparser.syntaxes.ElementMetadata;
import de.safti.skparser.syntaxes.SyntaxInfo;
import de.safti.skparser.syntaxes.expression.ExpressionInfo;
import de.safti.skparser.syntaxes.parsed.SyntaxElement;

public class Argument<T> {
    private final SyntaxElement expression;

    public Argument(SyntaxElement expression) {
        this.expression = expression;
    }

    public T evaluate(TriggerContext context, ElementMetadata metadata) {
        SyntaxInfo info = expression.getInfo();
        if(!(info instanceof ExpressionInfo expressionInfo)) throw new IllegalArgumentException("Got non-expression syntax passed into Argument!");

        //noinspection unchecked
        return (T) expressionInfo.handler().evaluate(context, metadata);
    }

    public T[] evaluateAll(TriggerContext context, ElementMetadata metadata) {
        SyntaxInfo info = expression.getInfo();
        if(!(info instanceof ExpressionInfo expressionInfo)) throw new IllegalArgumentException("Got non-expression syntax passed into Argument!");

        //noinspection unchecked
        return (T[]) expressionInfo.handler().evaluateAll(context, metadata);
    }


    public SyntaxElement getExpression() {
        return expression;
    }
}
