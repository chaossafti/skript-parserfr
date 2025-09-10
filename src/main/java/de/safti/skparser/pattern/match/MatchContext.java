package de.safti.skparser.pattern.match;

import de.safti.skparser.SkriptParser;

import java.util.ArrayList;
import java.util.List;

public class MatchContext {
    private final List<String> regexMatches = new ArrayList<>();
    private final SkriptParser parser;

    public MatchContext(SkriptParser parser) {
        this.parser = parser;
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
        return new MatchContext(parser);
    }

    public SkriptParser getParser() {
        return parser;
    }
}
