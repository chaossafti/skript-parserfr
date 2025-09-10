package de.safti.skparser.pattern.match;

public class MatchResult {
    private final boolean success;
    private final String remaining;
    private final MatchContext ctx;

    private MatchResult(boolean success, String remaining, MatchContext ctx) {
        this.success = success;
        this.remaining = remaining;
        this.ctx = ctx;
    }

    public static MatchResult success(String remaining, MatchContext ctx) {
        return new MatchResult(true, remaining, ctx);
    }

    public static MatchResult failure() {
        return new MatchResult(false, null, null);
    }

    public boolean isSuccess() { return success; }
    public String getRemaining() { return remaining; }
    public MatchContext getContext() { return ctx; }
}
