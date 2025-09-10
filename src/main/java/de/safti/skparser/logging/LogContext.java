package de.safti.skparser.logging;

import de.safti.skparser.Script;

public class LogContext {
    private Script script;
    private int line;

    public Script getScript() {
        return script;
    }

    public void setScript(Script script) {
        this.script = script;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }
}
