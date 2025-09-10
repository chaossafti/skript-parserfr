package de.safti.skparser.syntaxes.structure;

import de.safti.skparser.pattern.SyntaxPattern;
import de.safti.skparser.syntaxes.SyntaxInfo;

public record StructureInfo(SyntaxPattern pattern, int priority, StructureHandler handler) implements SyntaxInfo {

    public static StructureBuilder builder() {
        return new StructureBuilder();

    }

}
