package de.safti.skparser.test;

import de.safti.skparser.SkriptParser;
import de.safti.skparser.pattern.SyntaxPattern;
import de.safti.skparser.pattern.match.SyntaxMatchResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PatternTest {


    private static SkriptParser parser() {
        return Common.PARSER;
    }

    @Test
    public void testLiteral() {
        String literal = "test literal";

        SyntaxPattern pattern = SyntaxPattern.compile(literal);

        // same input matches
        Assertions.assertTrue(pattern.matches(literal, parser()));

        // allow multiple spaces
        Assertions.assertTrue(pattern.matches("test  literal", parser()));

        // allow prefix and suffix spaces
        Assertions.assertTrue(pattern.matches("  test literal", parser()));
        Assertions.assertTrue(pattern.matches("test literal  ", parser()));
        Assertions.assertTrue(pattern.matches("  test literal  ", parser()));

        // things that shouldn't match
        Assertions.assertFalse(pattern.matches("test", parser()));
        Assertions.assertFalse(pattern.matches("test lit", parser()));
        Assertions.assertFalse(pattern.matches("literal test", parser()));
        Assertions.assertFalse(pattern.matches("something completely different", parser()));

    }

    @Test
    public void testOptional() {
        String literal = "test [optional] pattern";

        SyntaxPattern pattern = SyntaxPattern.compile(literal);

        Assertions.assertTrue(pattern.matches("test pattern", parser()));
        Assertions.assertTrue(pattern.matches("test optional pattern", parser()));
        Assertions.assertTrue(pattern.matches("test optional   pattern", parser()));
        Assertions.assertTrue(pattern.matches(" test optional   pattern", parser()));

        Assertions.assertFalse(pattern.matches(" test optional", parser()));
        Assertions.assertFalse(pattern.matches(" something different", parser()));
    }

    @Test
    public void testRegex() {
        String literal = "test <.*(?=regex)> regex";

        SyntaxPattern pattern = SyntaxPattern.compile(literal);
        SyntaxMatchResult details = pattern.matchDetailed("test whatever regex", parser());

        Assertions.assertTrue(details.isSuccess());
        Assertions.assertEquals("whatever", details.getContext().getMatchAt(0));
    }

}
