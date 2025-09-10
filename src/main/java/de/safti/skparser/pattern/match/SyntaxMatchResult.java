package de.safti.skparser.pattern.match;

public class SyntaxMatchResult {
    private final boolean success;
    private final String remaining;
    private final MatchContext ctx;

    private SyntaxMatchResult(boolean success, String remaining, MatchContext ctx) {
        this.success = success;
        this.remaining = remaining;
        this.ctx = ctx;
    }

    public static SyntaxMatchResult success(String remaining, MatchContext ctx) {
        return new SyntaxMatchResult(true, remaining, ctx);
    }

    public static SyntaxMatchResult failure() {
        return new SyntaxMatchResult(false, null, null);
    }

    public boolean isSuccess() { return success; }
    public String getRemaining() { return remaining; }
    public MatchContext getContext() { return ctx; }
}
