package de.safti.skparser.std;

import de.safti.skparser.logging.AlertReceiver;
import de.safti.skparser.logging.LogContext;

public class ConsoleAlertReceiver implements AlertReceiver {
    @Override
    public void receive(String alertMessage, LogContext logContext) {
        System.err.println(alertMessage);
    }
}
