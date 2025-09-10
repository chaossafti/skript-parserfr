package de.safti.skparser.types;

import de.safti.skparser.pattern.SyntaxPattern;

public record Type(String codeName, SyntaxPattern pattern, Class<?> clazz) {


}
