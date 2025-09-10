package de.safti.skparser.syntaxes.expression;

import de.safti.skparser.pattern.SyntaxPattern;
import de.safti.skparser.syntaxes.SyntaxInfo;

public record ExpressionInfo(int priority, SyntaxPattern pattern, ExpressionHandler<?> handler) implements SyntaxInfo {

    public static <T> ExpressionBuilder<T> builder(Class<T> returnTypeClass) {
        return new ExpressionBuilder<>(returnTypeClass);
    }

}
