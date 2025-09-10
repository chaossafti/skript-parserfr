package de.safti.skparser.logging.errors;

public class CodeOutsideOfTriggerError implements Error {
    public static final String ID = "code outside of trigger";
    private final String message;

    public CodeOutsideOfTriggerError(String code) {
        this.message = code + " is not inside a trigger! Use 'on load' to run code when the script loads.";
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
