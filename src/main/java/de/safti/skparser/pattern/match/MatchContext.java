package de.safti.skparser.pattern.match;

import de.safti.skparser.SkriptParser;

import java.util.ArrayList;
import java.util.List;

public class MatchContext {
    private final List<String> regexMatches = new ArrayList<>();
    private final SkriptParser parser;
    private final boolean isRoot;
    private final String input;

    public MatchContext(SkriptParser parser, boolean isRoot, String input) {
        this.parser = parser;
        this.isRoot = isRoot;
        this.input = input;
    }

    public String getInput() {
        return input;
    }

    public boolean isRoot() {
        return isRoot;
    }

    public void pushRegexMatch(String regex) {
        regexMatches.add(regex);
    }

    public List<String> getRegexMatches() {
        return regexMatches;
    }

    public String getMatchAt(int index) {
        return regexMatches.get(index);
    }


    public MatchContext copy() {
        return new MatchContext(parser, isRoot, input);
    }

    public SkriptParser getParser() {
        return parser;
    }
}
