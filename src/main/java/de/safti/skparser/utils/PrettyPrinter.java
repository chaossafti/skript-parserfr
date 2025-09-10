package de.safti.skparser.utils;

import de.safti.skparser.pattern.PatternNode;
import de.safti.skparser.pattern.nodes.*;

public class PrettyPrinter {

    public static void printAST(PatternNode node) {
        printAST(node, 0);
    }

    private static void printAST(PatternNode node, int indent) {
        String pad = " ".repeat(indent * 4); // 4 spaces per indent

        switch (node) {
            case SequenceNode seq -> {
                System.out.println(pad + "sequence:");
                for (PatternNode child : seq.getNodes()) {
                    printAST(child, indent + 1);
                }
            }
            case OptionalNode opt -> {
                System.out.println(pad + "optional:");
                printAST(opt.getInner(), indent + 1);
            }
            case EitherNode either -> {
                System.out.println(pad + "either:");
                for (PatternNode option : either.getOptions()) {
                    printAST(option, indent + 1);
                }
            }
            case LiteralNode lit -> System.out.println(pad + lit.getLiteral());
            case TypeNode type -> System.out.println(pad + "%" + type.getExpectedTypeName() + "%");
            case RegexNode regex -> System.out.println(pad + "<" + regex.getPattern() + ">");
            //case FlagNode flag -> System.out.println(pad + "(flag " + flag.getIndex() + " " + flag.getOptions() + ")");
            case null, default -> System.out.println(pad + node); // fallback
        }
    }

}
