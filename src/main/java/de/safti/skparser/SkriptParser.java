package de.safti.skparser;

import de.safti.skparser.events.SkriptEventManager;
import de.safti.skparser.logging.SkriptLogger;
import de.safti.skparser.logging.errors.CodeOutsideOfTriggerError;
import de.safti.skparser.logging.errors.IllegalIndentionError;
import de.safti.skparser.logging.errors.UnknownSyntaxError;
import de.safti.skparser.pattern.match.SyntaxMatchResult;
import de.safti.skparser.runtime.TriggerContext;
import de.safti.skparser.std.ConsoleAlertReceiver;
import de.safti.skparser.std.elements.contexts.LoadContext;
import de.safti.skparser.syntaxes.SyntaxInfo;
import de.safti.skparser.syntaxes.SyntaxManager;
import de.safti.skparser.syntaxes.event.EventInfo;
import de.safti.skparser.syntaxes.event.EventStructureElement;
import de.safti.skparser.syntaxes.parsed.SyntaxElement;
import de.safti.skparser.syntaxes.parsed.StructureElement;
import de.safti.skparser.syntaxes.structure.StructureInfo;
import de.safti.skparser.types.TypeManager;
import org.jetbrains.annotations.NotNull;

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
    private final SkriptEventManager eventManager = new SkriptEventManager();


    public SkriptParser(SyntaxManager syntaxManager, TypeManager typeManager) {
        this.syntaxManager = syntaxManager;
        this.typeManager = typeManager;
    }

    // --------------------------
    // public api
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

            // log - TODO: be able to log somewhere else
            logger.flush(new ConsoleAlertReceiver());

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
    // parse root structures
    // --------------------------

    private @NotNull List<StructureElement> parseRootStructures(@NotNull List<String> lines, SkriptLogger logger) {
        List<StructureElement> structures = new ArrayList<>();

        StructureInfo currentStructureInfo = null;
        SyntaxManager.StructureParseResult currentParseResult = null;
        String currentLine = null;
        List<String> currentBodyLines = new ArrayList<>();

        for (String s : lines) {
            String line = s.stripTrailing();
            if (line.isBlank()) continue;

            // get indent; error and break if mix of spaces and tabs
            OptionalInt indentOpt = countIndent(s);
            if (indentOpt.isEmpty()) {
                String debugIndent = s.replaceAll(".*?(\\s*)$", "$1").replace(" ", "_").replace("\t", "->");
                logger.alert(new IllegalIndentionError(debugIndent));
                continue;
            }

            int indent = indentOpt.getAsInt();

            boolean isRoot = indent == 0;
            if (isRoot) {
                // Finalize previous structure
                finalizeRootStructure(structures, currentStructureInfo, currentParseResult, currentLine, currentBodyLines, logger);

                // Start new root structure
                currentParseResult = syntaxManager.matchStructure(line, this);
                if (!currentParseResult.syntaxMatchResult().isSuccess() || currentParseResult.structure() == null) {
                    logger.alert(new UnknownSyntaxError("Unknown structure: " + line.strip()));
                    currentStructureInfo = null;
                    currentBodyLines.clear();
                    continue;
                }

                currentStructureInfo = currentParseResult.structure();
                currentLine = line;
                currentBodyLines = new ArrayList<>();
            } else if (currentStructureInfo != null) {
                currentBodyLines.add(line);
            } else {
                logger.alert(new CodeOutsideOfTriggerError(line.strip()));
            }
        }

        // finalize last structure
        finalizeRootStructure(structures, currentStructureInfo, currentParseResult, currentLine, currentBodyLines, logger);

        return structures;
    }

    private void finalizeRootStructure(
            List<StructureElement> structures,
            StructureInfo structureInfo,
            SyntaxManager.StructureParseResult parseResult,
            String line,
            List<String> bodyLines,
            SkriptLogger logger
    ) {
        if (structureInfo == null || parseResult == null || line == null) return;

        StructureElement root;
        // TODO: make EventStructureElement also use StructureElement
        if(structureInfo instanceof EventInfo eventInfo) {
            root = new EventStructureElement(line, eventInfo, this, logger, null);
        } else {
            root = new StructureElement(line, structureInfo, this, logger, null);
        }

        List<SyntaxElement> bodyElements = new ArrayList<>();
        for (String bodyLine : bodyLines) {
            SyntaxElement element = parseElement(bodyLine, logger, root);

            if (element != null) {
                bodyElements.add(element);
            }
        }

        root.setBody(bodyElements);

        boolean success = structureInfo.handler().init(parseResult.syntaxMatchResult().getContext(), logger, root, root.getMetadata());
        if (success) structures.add(root);
    }

    // --------------------------
    // Parsing a single line into a SyntaxElement
    // --------------------------

    /**
     * Parses an element and initializes it.
     * It finds the SyntaxInfo from the {@link #syntaxManager} and creates a new SyntaxElement instance with it.
     * Calls the init method afterward
     */
    public SyntaxElement parseElement(String raw, SkriptLogger logger, StructureElement parent) {
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
        SyntaxElement element = new SyntaxElement(raw, elementInfo, this, logger, parent);

        boolean success = elementInfo.handler().init(matchResult.getContext(), logger, element, element.getMetadata());
        if(!success) {
            // TODO: make sure something was logged
            return null;
        }

        return element;
    }

    // --------------------------
    // helpers
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


    // --------------------------
    // getters
    // --------------------------

    public SyntaxManager getSyntaxManager() {
        return syntaxManager;
    }

    public TypeManager getTypeManager() {
        return typeManager;
    }

    public SkriptEventManager getEventManager() {
        return eventManager;
    }
}
