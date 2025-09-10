package de.safti.skparser.test;

import de.safti.skparser.Script;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

public class ScriptParseTest {

    @Test
    public void parseTest() {

        Script script = Common.PARSER.parseScript(Paths.get("src/test/resources/test.sk"));
        Assertions.assertNotNull(script);
        Assertions.assertEquals(1, script.getStructures().size());

    }

}
