package de.safti.skparser.syntaxes.effects;

import de.safti.skparser.bootstrap.SyntaxLoader;
import de.safti.skparser.logging.SkriptLogger;
import de.safti.skparser.pattern.PatternCompiler;
import de.safti.skparser.pattern.SyntaxPattern;
import de.safti.skparser.pattern.match.MatchContext;
import de.safti.skparser.runtime.TriggerContext;
import de.safti.skparser.syntaxes.ElementMetadata;
import de.safti.skparser.syntaxes.building.SyntaxInitHandler;
import de.safti.skparser.syntaxes.parsed.SyntaxElement;

public class EffectBuilder {

    private int priority;
    private SyntaxPattern pattern;
    private EffectHandler effectHandler;

    private SyntaxInitHandler initHandler;
    private EffectExecuteHandler effectExecuteHandler;

    // ------------------------
    // Builder setters
    // ------------------------

    public EffectBuilder priority(int priority) {
        this.priority = priority;
        return this;
    }

    public EffectBuilder pattern(SyntaxPattern pattern) {
        this.pattern = pattern;
        return this;
    }

    public EffectBuilder pattern(String pattern) {
        this.pattern = PatternCompiler.compile(pattern);
        return this;
    }

    public EffectBuilder handler(EffectHandler handler) {
        this.effectHandler = handler;
        return this;
    }

    public EffectBuilder initHandler(SyntaxInitHandler initHandler) {
        this.initHandler = initHandler;
        return this;
    }

    public EffectBuilder effectExecuteHandler(EffectExecuteHandler effectExecuteHandler) {
        this.effectExecuteHandler = effectExecuteHandler;
        return this;
    }

    // ------------------------
    // Build method
    // ------------------------

    public void register(SyntaxLoader loader) {
        EffectInfo info = build();
        loader.registerSyntax(info);
    }

    public EffectInfo build() {
        // Ensure the final handler is never null
        final SyntaxInitHandler finalInitHandler;

        if (this.initHandler == null) finalInitHandler = (matchContext, logger, element, metadata) -> true;
        else finalInitHandler = this.initHandler;

        EffectHandler effectHandler = this.effectHandler;
        if (effectHandler == null && this.effectExecuteHandler == null) {
            throw new IllegalArgumentException("Effect handler and effect execute handler cannot both be null!");
        }

        if(effectHandler == null) {
            effectHandler = new EffectHandler() {
                @Override
                public void execute(TriggerContext context, ElementMetadata metadata) {
                    effectExecuteHandler.execute(context, metadata);
                }

                @Override
                public boolean init(MatchContext context, SkriptLogger logger, SyntaxElement element, ElementMetadata metadata) {
                    return finalInitHandler.init(context, logger, element, metadata);
                }
            };
        }




        return new EffectInfo(priority, pattern, effectHandler);
    }
}