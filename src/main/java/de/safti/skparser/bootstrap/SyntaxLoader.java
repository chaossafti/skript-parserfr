package de.safti.skparser.bootstrap;

import de.safti.skparser.syntaxes.SyntaxInfo;
import de.safti.skparser.syntaxes.structure.StructureInfo;

import java.util.HashSet;
import java.util.Set;

public class SyntaxLoader {
    private final Set<StructureInfo> structures;
    private final Set<SyntaxInfo> elements;


    public SyntaxLoader() {
        structures = new HashSet<>();
        this.elements = new HashSet<>();

    }

    public void registerStructure(StructureInfo StructureInfo) {
        structures.add(StructureInfo);
    }

    public void registerSyntax(SyntaxInfo SyntaxInfo) {
        elements.add(SyntaxInfo);
    }


    public Set<StructureInfo> getStructures() {
        return structures;
    }

    public Set<SyntaxInfo> getElements() {
        return elements;
    }
}
