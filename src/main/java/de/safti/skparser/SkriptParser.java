package de.safti.skparser;

import de.safti.skparser.bootstrap.SyntaxLoader;
import de.safti.skparser.logging.SkriptLogger;
import de.safti.skparser.logging.errors.CodeOutsideOfTriggerError;
import de.safti.skparser.logging.errors.IllegalIndentionError;
import de.safti.skparser.logging.errors.UnknownSyntaxError;
import de.safti.skparser.pattern.match.SyntaxMatchResult;
import de.safti.skparser.runtime.TriggerContext;
import de.safti.skparser.std.elements.contexts.LoadContext;
import de.safti.skparser.syntaxes.SyntaxInfo;
import de.safti.skparser.syntaxes.SyntaxManager;
import de.safti.skparser.syntaxes.parsed.SyntaxElement;
import de.safti.skparser.syntaxes.parsed.StructureElement;
import de.safti.skparser.syntaxes.structure.StructureInfo;
import de.safti.skparser.syntaxes.structure.UninitializedStructure;
import de.safti.skparser.types.TypeManager;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

public class SkriptParser {

    private final ConcurrentMap<Path, Script> loadedScripts = new ConcurrentHashMap<>();
    private final SyntaxManager syntaxManager;
    private final TypeManager typeManager;

    public SkriptParser(SyntaxManager syntaxManager, TypeManager typeManager) {
        this.syntaxManager = syntaxManager;
        this.typeManager = typeManager;
    }

    // --------------------------
    // Public API
    // --------------------------

    public Script parseScript(Path path) {
        if(loadedScripts.containsKey(path)) {
            return loadedScripts.get(path);
        }

        try {
            String content = Files.readString(path);
            SkriptLogger logger = new SkriptLogger();
            Script script = parseScript(path, content, logger);

            // run on load event
            script.getStructures().stream()
                    .filter(structureElement -> structureElement.getRaw().equals("on load:"))
                    .forEach(structureElement -> {
                        TriggerContext context = new LoadContext();
                        structureElement.walk(context);
                    });

            loadedScripts.put(path, script);
            return script;

        } catch (IOException e) {
            throw new RuntimeException("Failed to parse script at: " + path, e);
        }
    }

    public Script parseScript(Path path, String content, SkriptLogger logger) {
        List<String> lines = content.lines().collect(Collectors.toList());
        List<StructureElement> structures = parseRootStructures(lines, logger);

        // Pass the list of structures into the Script
        return new Script(path, structures);
    }

    // --------------------------
    // Root-level structure parsing
    // --------------------------

    private List<StructureElement> parseRootStructures(List<String> lines, SkriptLogger logger) {
        // TODO: cleanup

        List<StructureElement> structures = new ArrayList<>();
        StructureInfo currentStructure = null;
        String structureLine = null;
        SyntaxManager.StructureParseResult structureParseResult = null;

        List<String> currentBodyLines = new ArrayList<>();

        for (String s : lines) {
            String line = s.stripTrailing();
            if(line.isBlank()) continue;

            OptionalInt indentOpt = countIndent(s);
            if(indentOpt.isEmpty()) {
                // matches all trailing spaces and tabs.
                String indentionDebugString = s.replaceAll(".*?(\\s*)$", "$1")
                        .replace(" ", "_")
                        .replace("\t", "->");

                logger.alert(new IllegalIndentionError(indentionDebugString));
                continue;
            }

            int indent = indentOpt.getAsInt();

            if(indent == 0) {
                // build previous structure
                if(currentStructure != null) {
                    StructureElement struct = buildStructure(structureParseResult, structureLine, currentBodyLines, logger);
                    // buildStructure has already logged
                    if(struct != null)
                        structures.add(struct);
                }

                // try to parse a structure
                structureParseResult = syntaxManager.matchStructure(line, this);
                if(!structureParseResult.syntaxMatchResult().isSuccess() || structureParseResult.structure() == null) {
                    logger.alert(new UnknownSyntaxError("Unknown structure: " + line.strip()));
                    continue;
                }

                currentStructure = structureParseResult.structure();
                structureLine = line;


                currentBodyLines = new ArrayList<>();
            } else if(currentStructure != null) {
                currentBodyLines.add(line);
            } else {
                logger.alert(new CodeOutsideOfTriggerError(line.strip()));
            }
        }

        // add last structure
        if(currentStructure != null) {
            StructureElement struct = buildStructure(structureParseResult, structureLine, currentBodyLines, logger);
            // buildStructure has already logged
            if(struct != null)
                structures.add(struct);
        }

        return structures;
    }

    @Nullable
    private StructureElement buildStructure(SyntaxManager.StructureParseResult parseResult, String raw, List<String> bodyLines, SkriptLogger logger) {
        // parse the body of the structure
        List<SyntaxElement> elements = parseBlock(bodyLines, logger);

        // initialize structure
        UninitializedStructure struct = new UninitializedStructure(raw, parseResult.structure(), elements);
        return struct.initialize(this, parseResult.syntaxMatchResult().getContext(), logger);
    }

    // --------------------------
    // Parsing a block of code lines
    // --------------------------

    private List<SyntaxElement> parseBlock(List<String> lines, SkriptLogger logger) {
        List<SyntaxElement> elements = new ArrayList<>();
        for (String line : lines) {
            SyntaxElement element = parseElement(line, logger);
            if(element == null) {
                // parseElement has already logged more informing error messages
                continue;
            }


            elements.add(element);
        }
        return elements;
    }

    // --------------------------
    // Parsing a single line into a SyntaxElement
    // --------------------------

    /**
     * Parses an element and initializes it.
     * It finds the SyntaxInfo from the {@link #syntaxManager} and creates a new SyntaxElement instance with it.
     * Calls the init method afterward
     */
    public SyntaxElement parseElement(String raw, SkriptLogger logger) {
        // parse
        SyntaxManager.ElementParseResult elementParseResult = syntaxManager.parseElement(raw, this);
        if(elementParseResult == null) {
            logger.alert(new UnknownSyntaxError(raw));
            return null;
        }

        SyntaxMatchResult matchResult = elementParseResult.syntaxMatchResult();
        SyntaxInfo elementInfo = elementParseResult.info();

        // make sure we have a successful match
        if(elementInfo == null || !matchResult.isSuccess()) {
            logger.alert(new UnknownSyntaxError(raw));
            return null;
        }

        // initialize the element
        SyntaxElement element = new SyntaxElement(raw, elementInfo, this, logger);
        boolean success = elementInfo.handler().init(matchResult.getContext(), logger, element.getMetadata());
        if(!success) return null;

        return element;
    }

    // --------------------------
    // Helpers
    // --------------------------

    private OptionalInt countIndent(String line) {
        if(line.isEmpty()) return OptionalInt.of(0);

        int spaces = 0;
        int tabs = 0;

        for (char c : line.toCharArray()) {
            if(c == ' ') spaces++;
            else if(c == '\t') tabs++;
            else break;
        }

        if(spaces > 0 && tabs > 0) return OptionalInt.empty();
        if(tabs > 0) return OptionalInt.of(tabs);
        if(spaces % 4 != 0) return OptionalInt.empty();
        return OptionalInt.of(spaces / 4);
    }

    public SyntaxManager getSyntaxManager() {
        return syntaxManager;
    }

    public TypeManager getTypeManager() {
        return typeManager;
    }
}
