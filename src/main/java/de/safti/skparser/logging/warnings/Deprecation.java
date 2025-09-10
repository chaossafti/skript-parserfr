package de.safti.skparser.logging.warnings;

import org.jetbrains.annotations.Nullable;

public class Deprecation implements Warning {
    public static final String ID = "deprecation";
    private final String message;

    public Deprecation(String component, @Nullable String replacement) {
        message = component + " is deprecated!" + (replacement == null ? " there is no replacement." : " Please use " + replacement + " instead!");

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
