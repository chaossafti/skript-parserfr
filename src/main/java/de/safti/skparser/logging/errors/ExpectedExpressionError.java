package de.safti.skparser.logging.errors;

public class ExpectedExpressionError implements Error{
    public static final String ID = "expected expression";
    private final String message;

    public ExpectedExpressionError(String rawSyntax) {
        this.message = rawSyntax + " is not an expression!";
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
