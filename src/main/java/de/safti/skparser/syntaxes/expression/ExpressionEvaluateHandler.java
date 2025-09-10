package de.safti.skparser.syntaxes.expression;

import de.safti.skparser.runtime.TriggerContext;

public interface ExpressionEvaluateHandler<T> {

    default T evaluate(TriggerContext context) {
        T[] ts = evaluateAll(context);
        if(ts != null && ts.length > 0)
            return ts[ts.length-1];
        return null;
    }

    T[] evaluateAll(TriggerContext context);

}
