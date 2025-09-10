package de.safti.skparser.syntaxes.expression;

import de.safti.skparser.bootstrap.SyntaxLoader;
import de.safti.skparser.logging.SkriptLogger;
import de.safti.skparser.pattern.PatternCompiler;
import de.safti.skparser.pattern.SyntaxPattern;
import de.safti.skparser.pattern.match.MatchContext;
import de.safti.skparser.runtime.TriggerContext;
import de.safti.skparser.syntaxes.ElementMetadata;
import de.safti.skparser.syntaxes.SyntaxInfo;
import de.safti.skparser.syntaxes.building.SyntaxInitHandler;
import org.jetbrains.annotations.Contract;

import java.util.Objects;

public class ExpressionBuilder<T> {
    public static final int DEFAULT_EXPRESSION_PRIORITY = 1000;

    private final Class<T> clazz;

    private SyntaxPattern pattern;
    private int priority = DEFAULT_EXPRESSION_PRIORITY;

    private ExpressionHandler<T> expressionHandler;
    private SyntaxInitHandler syntaxInitHandler;
    private ExpressionEvaluateHandler<T> evaluateHandler;

    public ExpressionBuilder(Class<T> clazz) {
        this.clazz = clazz;
    }

    // ------------------------
    // Setters
    // ------------------------

    public ExpressionBuilder<T> pattern(SyntaxPattern pattern) {
        this.pattern = pattern;
        return this;
    }

    public ExpressionBuilder<T> pattern(String pattern) {
        this.pattern = PatternCompiler.compile(pattern);
        return this;
    }

    public ExpressionBuilder<T> priority(int priority) {
        this.priority = priority;
        return this;
    }

    public ExpressionBuilder<T> handler(ExpressionHandler<T> handler) {
        this.expressionHandler = handler;
        return this;
    }

    public ExpressionBuilder<T> initHandler(SyntaxInitHandler handler) {
        this.syntaxInitHandler = handler;
        return this;
    }

    public ExpressionBuilder<T> evaluateHandler(ExpressionEvaluateHandler<T> handler) {
        this.evaluateHandler = handler;
        return this;
    }

    // ------------------------
    // Build method
    // ------------------------

    public void register(SyntaxLoader loader) {
        ExpressionInfo info = build();
        loader.registerSyntax(info);
    }


    @Contract(pure = true)
    public ExpressionInfo build() {
        Objects.requireNonNull(pattern, "Pattern must not be null");

        final SyntaxInitHandler finalSyntaxInitHandler;
        finalSyntaxInitHandler = Objects.requireNonNullElseGet(this.syntaxInitHandler, () -> (a, b, c) -> true);


        // Wrap the final handler if not provided
        ExpressionHandler<T> finalHandler = expressionHandler;

        if (finalHandler == null) {
            final ExpressionEvaluateHandler<T> evalHandler = this.evaluateHandler;
            Objects.requireNonNull(evalHandler, "ExpressionHandler must not be null, if expression handler is also null!");

            finalHandler = new ExpressionHandler<>() {

                @Override
                public boolean init(MatchContext context, SkriptLogger logger, ElementMetadata metadata) {
                    return finalSyntaxInitHandler.init(context, logger, metadata);
                }

                @Override
                public Class<T> typeClass() {
                    return clazz;
                }

                // Add evaluate methods if provided
                public T evaluate(TriggerContext context, ElementMetadata metadata) {
                    return evalHandler.evaluate(context);
                }

                public T[] evaluateAll(TriggerContext context, ElementMetadata metadata) {
                    return evalHandler.evaluateAll(context);
                }
            };
        }

        return new ExpressionInfo(priority, pattern, finalHandler);
    }
}
