package de.safti.skparser.logging.errors;

public class IllegalUsageError implements Error {
    public static final String ID = "illegal usage";
    private final String message;

    public IllegalUsageError(String raw, String incorrectUsage, String correctUsage) {
        this.message = raw + " is used wrongly!  It cannot be used " + incorrectUsage + " Use it " + correctUsage;
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
