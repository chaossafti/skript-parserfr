package de.safti.skparser.logging;

import de.safti.skparser.Script;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class SkriptLogger {
    private final Deque<Alert> alerts = new ArrayDeque<>();
    private final Set<String> suppressed = new HashSet<>();

    private final LogContext logContext = new LogContext();


    /**
     * Adds an alert id to the suppressed set.
     * See how suppression behaves at {@link #alert(Alert)}
     */
    public void addSuppressed(@NotNull String alertID) {
        suppressed.add(alertID);
    }

    /**
     * Removes the given alert id from the suppressed set.
     * See how suppression behaves at {@link #alert(Alert)}
     */
    public boolean removesSuppressed(@NotNull String alertID) {
        return suppressed.remove(alertID);
    }

    /*
     * LOG CONTEXT GETTERS / SETTERS
     */

    public void setScript(Script script) {
        logContext.setScript(script);
    }

    public Script getScript() {
        return logContext.getScript();
    }

    public void setLine(int line) {
        logContext.setLine(line);
    }

    public int getLine() {
        return logContext.getLine();
    }

    /*
     * LOGGER METHODS
     */

    /**
     * Queues an alert. <p>
     * Alerts will be sent to any receiver when {@link #flush(AlertReceiver)} is called.
     * The alert will not be queued if the alert id is suppressed, or {@link Alert#isEnabled()} returns false.
     *
     * @param alert the alert to queue.
     */
    public void alert(@NotNull Alert alert) {
        Objects.requireNonNull(alert);

        if(!alert.isEnabled()) return;
        if(suppressed.contains(alert.id())) return;

        alerts.add(alert);
    }

    /**
     * Sends all alerts queued to the provided receiver.
     *
     * @param receiver The receiver to send all alerts to.
     */
    public void flush(@NotNull AlertReceiver receiver) {
        Objects.requireNonNull(receiver);

        while(!alerts.isEmpty()) {
            Alert alert = alerts.pop();
            receiver.receive(alert, logContext);
        }
    }

}
