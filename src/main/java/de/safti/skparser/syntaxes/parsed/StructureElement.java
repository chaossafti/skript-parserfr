package de.safti.skparser.syntaxes.parsed;

import de.safti.skparser.syntaxes.SyntaxInfo;
import de.safti.skparser.syntaxes.structure.StructureInfo;

import java.util.List;
import java.util.Set;

public class Trigger extends SyntaxElement {
    private final List<SyntaxElement> body;

    public Trigger(StructureInfo info, List<SyntaxElement> body) {
        super(info);
        this.body = body;
    }

    public void walk() {
        getInfo().handler().walk(body);
    }

    @Override
    public StructureInfo getInfo() {
        return (StructureInfo) super.getInfo();
    }

    public List<SyntaxElement> getBody() {
        return body;
    }
}
