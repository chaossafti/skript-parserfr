package de.safti.skparser.test;

import de.safti.skparser.SkriptParser;
import de.safti.skparser.bootstrap.SyntaxLoader;
import de.safti.skparser.std.StandardSkript;
import de.safti.skparser.syntaxes.SyntaxManager;
import de.safti.skparser.types.TypeManager;

public class Common {
    public static final SkriptParser PARSER;

    static {

        SyntaxLoader loader = new SyntaxLoader();
        TypeManager typeManager = new TypeManager();

        // load default elements
        StandardSkript.register(loader, typeManager);


        SyntaxManager syntaxManager = new SyntaxManager(loader);

        PARSER = new SkriptParser(syntaxManager, typeManager);

    }

}
