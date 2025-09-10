package de.safti.skparser.logging;

public interface AlertReceiver {

    /**
     * Override {@link #receive(Alert, LogContext)} if you need more control.
     *
     * @param alert The alert message to send.
     */
    void receive(String alert, LogContext logContext);

    default void receive(Alert alert, LogContext logContext) {
        receive(alert.getFormatted(), logContext);
    }

}
