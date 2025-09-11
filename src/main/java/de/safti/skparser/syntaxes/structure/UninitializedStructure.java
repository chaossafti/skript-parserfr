package de.safti.skparser.syntaxes.structure;

import de.safti.skparser.SkriptParser;
import de.safti.skparser.logging.SkriptLogger;
import de.safti.skparser.pattern.match.MatchContext;
import de.safti.skparser.syntaxes.ElementMetadata;
import de.safti.skparser.syntaxes.event.EventInfo;
import de.safti.skparser.syntaxes.event.EventStructureElement;
import de.safti.skparser.syntaxes.parsed.StructureElement;
import de.safti.skparser.syntaxes.parsed.SyntaxElement;
import org.jetbrains.annotations.Nullable;

import java.util.List;

// TODO: remove
public class UninitializedStructure {
    private final String raw;
    private final StructureInfo info;
    private final List<SyntaxElement> body;

    public UninitializedStructure(String raw, StructureInfo structure, List<SyntaxElement> body) {
        this.raw = raw;
        this.info = structure;
        this.body = body;
    }

    public StructureInfo getInfo() {
        return info;
    }

    public List<SyntaxElement> getBody() {
        return body;
    }

    @Nullable
    public StructureElement initialize(SkriptParser parser, MatchContext context, SkriptLogger logger) {
        StructureElement element;
        if(info instanceof EventInfo eventInfo) {
            element = new EventStructureElement(raw, eventInfo, body, parser, logger);
        } else {
            element = new StructureElement(raw, info, body, parser, logger);
        }

        boolean success =  info.handler().init(context, logger, element, element.getMetadata());
        if(!success) return null;

        return element;
    }
}
