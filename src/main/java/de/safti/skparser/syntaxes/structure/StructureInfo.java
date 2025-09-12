package de.safti.skparser.syntaxes.structure;

import de.safti.skparser.pattern.SyntaxPattern;
import de.safti.skparser.syntaxes.SyntaxInfo;
import de.safti.skparser.syntaxes.event.EventStructureElement;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * The base class for all type of structures.
 * A structure is any kind of code block, either at root level or outside of it.
 * By itself, structures are never triggered.
 *
 * @see EventStructureElement
 */
public class StructureInfo implements SyntaxInfo {
    private final SyntaxPattern pattern;
    private final int priority;
    private final StructureHandler handler;

    public StructureInfo(SyntaxPattern pattern, int priority, StructureHandler handler) {
        this.pattern = pattern;
        this.priority = priority;
        this.handler = handler;
    }

    public static StructureBuilder structureBuilder() {
        return new StructureBuilder();

    }

    @Override
    public @NotNull SyntaxPattern pattern() {
        return pattern;
    }

    @Override
    public int priority() {
        return priority;
    }

    @Override
    public StructureHandler handler() {
        return handler;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == this) return true;
        if(obj == null || obj.getClass() != this.getClass()) return false;
        var that = (StructureInfo) obj;
        return Objects.equals(this.pattern, that.pattern) &&
                this.priority == that.priority &&
                Objects.equals(this.handler, that.handler);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pattern, priority, handler);
    }

    @Override
    public String toString() {
        return "StructureInfo[" +
                "pattern=" + pattern + ", " +
                "priority=" + priority + ", " +
                "handler=" + handler + ']';
    }


}
