package de.safti.skparser;

import de.safti.skparser.syntaxes.parsed.StructureElement;
import de.safti.skparser.syntaxes.structure.StructureInfo;

import java.nio.file.Path;
import java.util.List;

// TODO: unload/reload feature
public class Script {
    private final Path path;
    private final List<StructureElement> structures;

    public Script(Path path, List<StructureElement> structures) {
        this.path = path;
        this.structures = structures;
    }

    public void unload() {
        for (StructureElement structure : structures) {
            StructureInfo info = structure.getInfo();
            info.handler().unload(structure);
        }
    }

    public Path getPath() {
        return path;
    }

    public List<StructureElement> getStructures() {
        return structures;
    }
}
