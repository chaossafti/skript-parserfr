package de.safti.skparser.syntaxes.parsed;

import de.safti.skparser.SkriptParser;
import de.safti.skparser.logging.SkriptLogger;
import de.safti.skparser.runtime.TriggerContext;
import de.safti.skparser.syntaxes.structure.StructureInfo;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class StructureElement extends SyntaxElement {
    private final List<SyntaxElement> body;

    public StructureElement(String raw, StructureInfo info, List<SyntaxElement> body, SkriptParser parser, SkriptLogger logger) {
        super(raw, info, parser, logger);
        this.body = body;
    }

    public void walk(TriggerContext context) {
        getInfo().handler().walk(body, context, metadata);
    }

    @Override
    public @NotNull StructureInfo getInfo() {
        return (StructureInfo) super.getInfo();
    }

    public List<SyntaxElement> getBody() {
        return body;
    }
}
