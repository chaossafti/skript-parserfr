package de.safti.skparser.logging.errors;

public class IllegalIndentionError implements Error {
    public static final String ID = "illegal indention";
    private final String message;

    public IllegalIndentionError(String indentionString) {
        this.message = "You cannot use both spaces and tabs! Found: " + indentionString;
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
