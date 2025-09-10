package de.safti.skparser.syntaxes.effects;

import de.safti.skparser.pattern.SyntaxPattern;
import de.safti.skparser.syntaxes.SyntaxInfo;
import org.jetbrains.annotations.NotNull;

public record EffectInfo(int priority, SyntaxPattern pattern, EffectHandler handler) implements SyntaxInfo {

    public static EffectBuilder builder() {
        return new EffectBuilder();
    }


}
