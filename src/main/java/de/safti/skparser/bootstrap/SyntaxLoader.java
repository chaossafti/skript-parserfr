package de.safti.skparser.bootstrap;

import de.safti.skparser.syntaxes.SyntaxInfo;
import de.safti.skparser.syntaxes.event.EventValue;
import de.safti.skparser.syntaxes.structure.StructureInfo;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SyntaxLoader {
    private final Set<StructureInfo> structures;
    private final Set<SyntaxInfo> elements;
    private final Map<String, EventValue<?, ?>> eventValues;


    public SyntaxLoader() {
        structures = new HashSet<>();
        this.elements = new HashSet<>();
        eventValues = new HashMap<>();
    }

    public void registerStructure(StructureInfo StructureInfo) {
        structures.add(StructureInfo);
    }

    public void registerSyntax(SyntaxInfo SyntaxInfo) {
        elements.add(SyntaxInfo);
    }

    public void registerEventValue(EventValue<?, ?> eventValue) {
        eventValues.put(eventValue.getName(), eventValue);
    }


    public Set<StructureInfo> getStructures() {
        return structures;
    }

    public Set<SyntaxInfo> getElements() {
        return elements;
    }

    @NotNull
    public Map<String, EventValue<?, ?>> getEventValues() {
        return eventValues;
    }
}
