package de.safti.skparser.logging.errors;

public class IllegalTypeError implements Error {
    public static String ID = "illegal type";

    private final String message;

    public IllegalTypeError(String syntax, String providedType, String expectedType) {
        this.message = syntax + " expected type " + expectedType + " but got " + providedType;
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
