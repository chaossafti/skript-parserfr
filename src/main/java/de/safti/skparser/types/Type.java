package de.safti.skparser.types;

import de.safti.skparser.SkriptParser;
import de.safti.skparser.pattern.SyntaxPattern;

public record Type(String codeName, SyntaxPattern pattern, Class<?> clazz) {


    /**
     * Checks whether this type can accept a value of the given expected type.

     * @param parser           the SkriptParser instance to resolve types
     * @param expectedTypeName the name of the expected type
     * @return true if the other type's class can be assigned to this type's class; false otherwise
     */
    public boolean acceptsType(SkriptParser parser, String expectedTypeName) {
        Type other = parser.getTypeManager().getTypeByMatch(expectedTypeName, parser);
        if (other == null || other.clazz == null) return false;

        System.out.println("clazz = " + clazz);
        return this.clazz.isAssignableFrom(other.clazz);
    }


    /**
     * Checks whether a value of this type can be assigned to a variable of the given expected type.
     * Essentially the counterpart of {@link #acceptsType(SkriptParser, String)}
     *
     * @param parser           the SkriptParser instance to resolve types
     * @param expectedTypeName the name of the target type
     * @return true if this type's class can be assigned to the target type's class; false otherwise
     */
    public boolean isAssignableTo(SkriptParser parser, String expectedTypeName) {
        Type other = parser.getTypeManager().getTypeByMatch(expectedTypeName, parser);
        if (other == null || other.clazz == null) return false;

        return other.clazz.isAssignableFrom(this.clazz);
    }
}
