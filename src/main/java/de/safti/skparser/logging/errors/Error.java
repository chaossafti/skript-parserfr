package de.safti.skparser.logging.errors;

import de.safti.skparser.logging.Alert;
import org.jetbrains.annotations.ApiStatus;

/**
 * Represents an Error.
 * Implement to make your own error.
 */
public interface Error extends Alert {

    @Override
    @ApiStatus.NonExtendable
    default boolean isSuppressible() {
        return false;
    }
}
