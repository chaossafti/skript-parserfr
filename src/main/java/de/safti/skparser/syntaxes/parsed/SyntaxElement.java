package de.safti.skparser.syntaxes.parsed;

import de.safti.skparser.SkriptParser;
import de.safti.skparser.logging.SkriptLogger;
import de.safti.skparser.pattern.SyntaxPattern;
import de.safti.skparser.pattern.TypeMatchNode;
import de.safti.skparser.runtime.arguments.Argument;
import de.safti.skparser.syntaxes.ElementMetadata;
import de.safti.skparser.syntaxes.SyntaxInfo;
import de.safti.skparser.syntaxes.expression.ExpressionInfo;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SyntaxElement {
    protected final SyntaxInfo info;
    protected final ElementMetadata metadata;

    protected final String raw;
    protected final SkriptParser parser;

    private final StructureElement parent;

    public SyntaxElement(String raw, SyntaxInfo info, SkriptParser parser, SkriptLogger logger, StructureElement parent) {
        this.raw = raw;
        this.info = info;
        this.parser = parser;
        this.parent = parent;


        SyntaxPattern pattern = info.pattern();

        // collect arguments
        List<Argument<?>> arguments = new ArrayList<>();
        List<TypeMatchNode> matchedTypeNodes = pattern.extractTypeMatches(this, parser, logger);
        for (TypeMatchNode typeMatch : matchedTypeNodes) {
            // Extract the raw substring matched for this type

            // parse sub string and sanity check
            SyntaxElement element = typeMatch.element();
            if(element == null || !(element.getInfo() instanceof ExpressionInfo)) {
                // this should always be true as the pattern only matches expression.
                // if anything does go wrong, lets print the raw content of this syntax element for debugging.
                throw new InternalError(" RAW: " + raw);
            }

            // Wrap it as an Argument with the TypeNode info
            arguments.add(new Argument<>(element));
        }


        metadata = new ElementMetadata(arguments, raw);
    }

    @Nullable
    public StructureElement getParent() {
        return parent;
    }

    /**
     * Returns the root {@link StructureElement}.
     *
     *
     * @return the root {@link StructureElement}
     */
    @NotNull
    public StructureElement findRoot() {
        if (parent == null) {
            // By contract, root elements are always StructureElement
            return (StructureElement) this;
        }

        StructureElement current = parent;
        while (current.getParent() != null) {
            current = current.getParent();
        }
        return current;
    }
    public SkriptParser getParser() {
        return parser;
    }

    @NotNull
    public SyntaxInfo getInfo() {
        return info;
    }

    @NotNull
    public String getRaw() {
        return raw;
    }

    @NotNull
    public ElementMetadata getMetadata() {
        return metadata;
    }

}
