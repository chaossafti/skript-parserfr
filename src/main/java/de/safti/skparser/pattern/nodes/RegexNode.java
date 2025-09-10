package de.safti.skparser.pattern.nodes;

import de.safti.skparser.logging.SkriptLogger;
import de.safti.skparser.pattern.PatternNode;
import de.safti.skparser.pattern.TypeMatchNode;
import de.safti.skparser.pattern.match.MatchContext;
import de.safti.skparser.pattern.match.SyntaxMatchResult;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexNode implements PatternNode {
    private final Pattern regex;

    public RegexNode(String regex) {
        this.regex = Pattern.compile("^(" + regex + ")");
    }

    @Override
    public SyntaxMatchResult matches(String input, MatchContext ctx) {
        input = input.stripLeading(); // skip leading whitespace

        Matcher m = regex.matcher(input);
        if (m.find()) {
            String matched = m.group(1);
            String remaining = input.substring(m.end());

            ctx.pushRegexMatch(matched.trim());

            return SyntaxMatchResult.success(remaining, ctx);
        }

        return SyntaxMatchResult.failure();
    }

    @Override
    public int matchAndCollectTypes(@NotNull String input, int startIndex,
                                    @NotNull List<TypeMatchNode> matches,
                                    @NotNull MatchContext context, SkriptLogger logger) {
        Matcher matcher = regex.matcher(input);
        matcher.region(startIndex, input.length());
        if (matcher.lookingAt()) {
            return startIndex + matcher.end() - matcher.start();
        }
        return -1;
    }

    @Override
    public String toString() {
        return "<" + regex.pattern() + ">";
    }

    @NotNull
    public String getPattern() {
        return regex.pattern();
    }
}
