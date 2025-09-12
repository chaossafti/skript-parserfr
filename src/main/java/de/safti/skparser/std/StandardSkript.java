package de.safti.skparser.std;

import de.safti.skparser.bootstrap.SyntaxLoader;
import de.safti.skparser.std.elements.expressions.ExprEventValue;
import de.safti.skparser.std.elements.expressions.ExprStringLiteral;
import de.safti.skparser.syntaxes.effects.EffectInfo;
import de.safti.skparser.syntaxes.event.EventInfo;
import de.safti.skparser.syntaxes.expression.ExpressionInfo;
import de.safti.skparser.syntaxes.structure.StructureInfo;
import de.safti.skparser.types.TypeManager;

public class StandardSkript {


    public static void register(SyntaxLoader loader, TypeManager typeManager) {
        /*
         * STRUCTURES
         */

        StructureInfo.structureBuilder()
                .pattern("on load")
                .register(loader);

        /*
         * LITERALS
         */

        ExpressionInfo.builder(String.class)
                .pattern(ExprStringLiteral.PATTERN)
                .handler(new ExprStringLiteral())
                .register(loader);


        /*
         * EXPRESSIONS
         */

        ExpressionInfo.builder(Object.class)
                .pattern(ExprEventValue.PATTERN)
                .handler(new ExprEventValue())
                .register(loader);


        /*
         * EFFECTS
         */

        EffectInfo.builder()
                .pattern("print %string%")
                .initHandler((context, logger, element, metadata) -> true)
                .effectExecuteHandler((context, metadata) -> {
                    System.out.println(metadata.evaluateArgument(0, context, String.class));
                })
                .register(loader);

        /*
         * TYPES
         */

        typeManager.register(String.class, "string", "string");
        typeManager.register(Object.class, "object", "object");


    }

}
