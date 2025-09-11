package de.safti.skparser.syntaxes.structure;

import de.safti.skparser.bootstrap.SyntaxLoader;
import de.safti.skparser.pattern.PatternCompiler;
import de.safti.skparser.pattern.SyntaxPattern;
import de.safti.skparser.syntaxes.building.SyntaxInitHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class StructureBuilder {
    public static final int DEFAULT_STRUCTURE_PRIORITY = 5000;

    private SyntaxPattern syntaxPattern;
    private int priority = DEFAULT_STRUCTURE_PRIORITY;
    private StructureHandler structureHandler;
    private SyntaxInitHandler initHandler;

    StructureBuilder() {

    }

    public StructureBuilder pattern(SyntaxPattern syntaxPattern) {
        this.syntaxPattern = syntaxPattern;
        return this;
    }

    public StructureBuilder pattern(String syntaxPattern) {
        this.syntaxPattern = PatternCompiler.compile(syntaxPattern);
        return this;
    }

    public SyntaxPattern pattern() {
        return syntaxPattern;
    }

    public StructureBuilder priority(int priority) {
        this.priority = priority;
        return this;
    }

    public int priority() {
        return priority;
    }

    public StructureBuilder handler(StructureHandler handler) {
        this.structureHandler = handler;
        return this;
    }

    public StructureBuilder initHandler(SyntaxInitHandler initHandler) {
        this.initHandler = initHandler;
        return this;
    }

    public void register(@NotNull SyntaxLoader syntaxLoader) {
        Objects.requireNonNull(syntaxLoader);
        StructureInfo structureInfo = build();

        syntaxLoader.registerStructure(structureInfo);
    }

    /**
     * Builds a structure info. If no pattern was provided, this method throws an exception.
     * If no structure handler was provided, there will be one created from the init handler.
     * If the init handler is null, it will default to always returning true.
     *
     * @return The resulting StructureInfo.
     */
    public StructureInfo build() {
        Objects.requireNonNull(syntaxPattern);

        // Ensure handler is never null
        StructureHandler finalHandler = this.structureHandler;

        if (finalHandler == null) {
            if (initHandler != null) {
                // Wrap SyntaxInitHandler in a StructureHandler
                finalHandler = (context, logger, element, metadata) -> initHandler.init(context, logger, element, element.getMetadata());
            } else {
                // fallback empty handler
                finalHandler = (context, logger, element, metadata) -> true;
            }
        }

        return new StructureInfo(syntaxPattern, priority, finalHandler);
    }



}
