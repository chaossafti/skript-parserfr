package de.safti.skparser.syntaxes;

import de.safti.skparser.SkriptParser;
import de.safti.skparser.bootstrap.SyntaxLoader;
import de.safti.skparser.pattern.SyntaxPattern;
import de.safti.skparser.pattern.match.SyntaxMatchResult;
import de.safti.skparser.syntaxes.expression.ExpressionHandler;
import de.safti.skparser.syntaxes.structure.StructureInfo;
import de.safti.skparser.types.TypeManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// TODO: events, conditionals
public class SyntaxManager {
    // all of these fields should be immutable
    private final List<StructureInfo> structures;
    private final List<SyntaxInfo> elements;

    public SyntaxManager(SyntaxLoader syntaxLoader, TypeManager typeManager) {
        // structures
        List<StructureInfo> unsortedStructures = new ArrayList<>(syntaxLoader.getStructures());
        unsortedStructures.sort(SyntaxInfo::compareTo);
        structures = List.copyOf(unsortedStructures);

        // any other elements
        List<SyntaxInfo> unsortedElements = new ArrayList<>(syntaxLoader.getElements());
        unsortedElements.sort(SyntaxInfo::compareTo);
        elements = List.copyOf(unsortedElements);

        // see if any expressions return a type class that doesn't have a registered type.
        Map<Class<?>, List<SyntaxInfo>> unregisteredTypesMap = elements.stream()
                .filter(syntaxInfo -> syntaxInfo.handler() instanceof ExpressionHandler<?>)
                .collect(Collectors.groupingBy(
                        info -> ((ExpressionHandler<?>) info.handler()).typeClass(),
                        Collectors.toList()
                ));

        Map<Class<?>, List<SyntaxInfo>> unregisteredTypeClasses = unregisteredTypesMap.entrySet().stream()
                .filter(e -> typeManager.getTypeByClass(e.getKey()) == null)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        if (!unregisteredTypeClasses.isEmpty()) {
            // found unregistered type; throw an exception
            StringBuilder sb = new StringBuilder("not all used types have been registered!\n");
            unregisteredTypeClasses.forEach((clazz, infos) -> {
                sb.append(clazz.getName()).append(" used by: ")
                        .append(infos.stream()
                                .map(Object::toString) // or customize how SyntaxInfo is represented
                                .collect(Collectors.joining(", ")))
                        .append("\n");
            });
            throw new IllegalStateException(sb.toString());
        }
    }


    public List<StructureInfo> getStructures() {
        return structures;
    }

    public record StructureParseResult(@Nullable StructureInfo structure, @NotNull SyntaxMatchResult syntaxMatchResult) {

    }

    public record ElementParseResult(@Nullable SyntaxInfo info, @NotNull SyntaxMatchResult syntaxMatchResult) {

    }

    @NotNull
    public StructureParseResult matchStructure(String raw, SkriptParser parser) {
        for (StructureInfo structure : structures) {
            // match pattern
            SyntaxPattern pattern = structure.pattern();
            SyntaxMatchResult details = pattern.matchDetailed(raw, parser);
            if(!details.isSuccess()) continue;

            // successfully found structure
            return new StructureParseResult(structure, details);
        }

        return new StructureParseResult(null, SyntaxMatchResult.failure());
    }

    @Nullable
    public ElementParseResult parseElement(String raw, SkriptParser parser) {
        for (SyntaxInfo info : elements) {
            // match pattern
            SyntaxPattern pattern = info.pattern();
            SyntaxMatchResult details = pattern.matchDetailed(raw, parser);
            if(!details.isSuccess()) continue;

            // successfully found info
            return new ElementParseResult(info, details);
        }

        return null;
    }
}
