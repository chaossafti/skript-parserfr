package de.safti.skparser.pattern;

import de.safti.skparser.pattern.nodes.*;

import java.util.*;

public class PatternCompiler {

    private final String raw;
    private int pos;

    private PatternCompiler(String raw) {
        this.raw = raw;
        this.pos = 0;
    }

    public static SyntaxPattern compile(String raw) {
        PatternCompiler pc = new PatternCompiler(raw);
        PatternNode root = pc.parseSequence();
        return new SyntaxPattern(raw, root);
    }

    private boolean hasNext() {
        return pos < raw.length();
    }

    private char peek() {
        return raw.charAt(pos);
    }

    private char next() {
        return raw.charAt(pos++);
    }

    /**
     * Parse a sequence of nodes until the end of input or a closing bracket/parenthesis.
     * Ignores all whitespace in the pattern itself.
     */
    private PatternNode parseSequence() {
        List<PatternNode> nodes = new ArrayList<>();
        StringBuilder buf = new StringBuilder();

        while (hasNext()) {
            char c = peek();

            switch (c) {
                case '[' -> { // optional
                    flushLiteral(nodes, buf);
                    next();
                    PatternNode inner = parseSequence();
                    expect(']');
                    nodes.add(new
                            OptionalNode(inner));
                }
                case '(' -> { // either or flag
                    flushLiteral(nodes, buf);
                    next();
                    nodes.add(parseEitherOrFlag());
                }
                case '%' -> { // type
                    flushLiteral(nodes, buf);
                    next();
                    String typeName = readUntil('%');
                    expect('%');
                    nodes.add(new TypeNode(typeName));
                }
                case '<' -> { // regex
                    flushLiteral(nodes, buf);
                    next();
                    String regex = readUntil('>');
                    expect('>');
                    nodes.add(new RegexNode(regex));
                }
                case ']', ')' -> { // end of optional / either
                    flushLiteral(nodes, buf);
                    return nodes.size() == 1 ? nodes.getFirst() : new SequenceNode(nodes);
                }
                default -> buf.append(next());
            }
        }

        flushLiteral(nodes, buf);
        return nodes.size() == 1 ? nodes.getFirst() : new SequenceNode(nodes);
    }

    private void flushLiteral(List<PatternNode> nodes, StringBuilder buf) {
        if (buf.isEmpty()) return;

        // Convert buffer to string and reset
        String literal = buf.toString();
        buf.setLength(0);

        // Split by whitespace, ignoring empty strings
        String[] tokens = literal.trim().split("\\s+");
        for (String token : tokens) {
            if (!token.isEmpty()) {
                nodes.add(new LiteralNode(token));
            }
        }
    }

    private void expect(char expected) {
        if (!hasNext() || next() != expected) {
            throw new IllegalStateException("Expected '" + expected + "' at pos " + pos + " in " + raw);
        }
    }

    private String readUntil(char end) {
        StringBuilder sb = new StringBuilder();
        while (hasNext() && peek() != end) {
            sb.append(next());
        }
        return sb.toString();
    }

    /**
     * Parse either (a|b|c) or flag (1=important|unimportant)
     */
    private PatternNode parseEitherOrFlag() {
        List<PatternNode> options = new ArrayList<>();
        List<PatternNode> current = new ArrayList<>();
        StringBuilder buf = new StringBuilder();

        while (hasNext()) {
            char c = peek();
            if (c == ')') {
                flushLiteral(current, buf);
                if (!current.isEmpty()) {
                    options.add(current.size() == 1 ? current.getFirst() : new SequenceNode(current));
                }
                next();
                return new EitherNode(options); // for now, treat all as EitherNode
            } else if (c == '|') {
                flushLiteral(current, buf);
                if (!current.isEmpty()) {
                    options.add(current.size() == 1 ? current.getFirst() : new SequenceNode(current));
                }
                current.clear();
                next();
            } else {
                current.add(parseSequence());
            }
        }

        throw new IllegalStateException("Unclosed '(' in pattern: " + raw);
    }
}
