package de.safti.skparser.syntaxes.parsed;

import de.safti.skparser.SkriptParser;
import de.safti.skparser.logging.SkriptLogger;
import de.safti.skparser.pattern.SyntaxPattern;
import de.safti.skparser.pattern.TypeMatchNode;
import de.safti.skparser.runtime.arguments.Argument;
import de.safti.skparser.syntaxes.ElementMetadata;
import de.safti.skparser.syntaxes.SyntaxInfo;
import de.safti.skparser.syntaxes.expression.ExpressionInfo;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SyntaxElement {
    private final String raw;
    private final SyntaxInfo info;
    private final SkriptParser parser;
    protected final ElementMetadata metadata;


    public SyntaxElement(String raw, SyntaxInfo info, SkriptParser parser, SkriptLogger logger) {
        this.raw = raw;
        this.info = info;
        this.parser = parser;


        SyntaxPattern pattern = info.pattern();

        // collect arguments
        List<Argument<?>> arguments = new ArrayList<>();
        List<TypeMatchNode> matchedTypeNodes = pattern.extractTypeMatches(raw, parser, logger);
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
