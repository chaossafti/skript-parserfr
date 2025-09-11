package de.safti.skparser.std;

import de.safti.skparser.bootstrap.SyntaxLoader;
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
         * EVENTS
         */



        EventInfo.eventBuilder()
                .pattern("on test")
                .contextType(TestContext.class)
                .register(loader);

        /*
         * LITERALS
         */

        ExpressionInfo.builder(String.class)
                .pattern(ExprStringLiteral.PATTERN)
                .handler(new ExprStringLiteral())
                .register(loader);


        /*
         * EFFECTS
         */

        EffectInfo.builder()
                .pattern("print %string%")
                .initHandler((context, logger, element, metadata) -> true)
                .effectExecuteHandler((context, metadata) -> System.out.println(metadata.evaluateArgument(0, context, String.class)))
                .register(loader);

        /*
         * TYPES
         */

        typeManager.register(String.class, "string", "string");


    }

}
