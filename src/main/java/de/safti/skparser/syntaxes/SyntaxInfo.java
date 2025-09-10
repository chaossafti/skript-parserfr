package de.safti.skparser.syntaxes;

import de.safti.skparser.pattern.SyntaxPattern;
import org.jetbrains.annotations.NotNull;

public interface SyntaxInfo extends Comparable<SyntaxInfo> {

    int priority();

    @NotNull
    SyntaxPattern pattern();

    SyntaxHandler handler();

    /**
     * Compares this to the given parameter.
     * The SyntaxInfo with the higher priority is the greater one.
     */
    @Override
    default int compareTo(@NotNull SyntaxInfo o) {
        // higher priorities first
        return Integer.compare(o.priority(), priority());
    }

}
