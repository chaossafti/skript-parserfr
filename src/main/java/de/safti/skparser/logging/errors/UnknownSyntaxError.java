package de.safti.skparser.logging.errors;

public class UnknownSyntaxError implements Error {
    public static final String ID = "unknown syntax";

    private final String message;

    public UnknownSyntaxError(String rawSyntax) {
        this.message = "Unknown syntax: " + rawSyntax;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public String id() {
        return ID;
    }
}
