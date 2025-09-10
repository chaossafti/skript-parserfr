package de.safti.skparser.logging;

import de.safti.skparser.logging.errors.Error;
import de.safti.skparser.logging.warnings.Warning;

/**
 * The parent class of anything that may be logged when running.
 * Extend to make your own class.
 *
 * @see Error
 * @see Warning
 */
public interface Alert {
    // TODO: debug

    /**
     * @return The raw warning message of this alert
     */
    String getMessage();

    default String getFormatted() {
        return getMessage(); // TODO: formatter provider
    }

    /**
     * @return The id that may be used to suppress this alert
     */
    String id();

    /**
     * @return True if this error may be suppressed, false otherwise.
     */
    default boolean isSuppressible() {
        return true;
    }

    /**
     * @return true if the alert should be logged, false otherwise.
     */
    default boolean isEnabled() {
        return true;
    }

}
