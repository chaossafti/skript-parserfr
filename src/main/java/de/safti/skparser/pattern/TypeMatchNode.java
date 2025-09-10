package de.safti.skparser.pattern;

import de.safti.skparser.pattern.nodes.TypeNode;
import de.safti.skparser.syntaxes.parsed.SyntaxElement;

public record TypeMatchNode(SyntaxElement element, TypeNode node, int startIndex, int endIndex) {

}