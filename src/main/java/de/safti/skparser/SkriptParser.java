package de.safti.skparser.syntaxes;

import de.safti.skparser.Script;

import java.nio.file.Path;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class SkriptParser {
    private final ConcurrentMap<Path, Script> loadedScripts = new ConcurrentHashMap<>();

    public SkriptParser() {
        
    }

    public Script parse(Path path) {
        if(loadedScripts.containsKey(path)) {
            return loadedScripts.get(path);
        }

        return new Script(path);
    }

}
