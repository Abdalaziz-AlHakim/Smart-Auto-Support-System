package behavioral.observer;

import javax.swing.JTextArea;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
// Updated by Fady
/**
 * Observer that appends ticket events to the GUI's real-time log panel.
 *
 * PATTERN: Observer (Concrete Observer)
 * Registered with TicketSystem at startup. Every time a ticket is created,
 * escalated, or resolved, this listener formats a timestamped message and
 * appends it to the Swing JTextArea passed at construction time.
 */
public class LogPanelListener implements TicketEventListener {

    /** The Swing text area in MainGUI where event messages are appended. */
    private final JTextArea logArea;

    /** Formats times as HH:mm:ss for the log prefix. */
    private static final DateTimeFormatter TIME_FMT =
            DateTimeFormatter.ofPattern("HH:mm:ss");

    /**
     * @param logArea The GUI text area to write log entries into. Must not be null.
     */
    public LogPanelListener(JTextArea logArea) {
        this.logArea = logArea;
    }

    /**
     * Receive a ticket event and append a formatted line to the log panel.
     * Uses emoji indicators so agents can scan the log at a glance:
     *   ✅ CREATED | ⬆ ESCALATED | ✔ RESOLVED
     */
    @Override
    public void onTicketEvent(TicketEvent event) {
        // Choose an emoji based on the event type for quick visual scanning
        String icon = switch (event.getType()) {
            case CREATED   -> "✅";
            case ESCALATED -> "⬆";
            case RESOLVED  -> "✔";
            case REOPENED  -> "♻";
        };

        String time = LocalTime.now().format(TIME_FMT);
        String msg  = String.format("[%s] %s Ticket #%d \"%s\" — %s%n",
                time, icon,
                event.getTicket().getId(),
                event.getTicket().getTitle(),
                event.getType());

        // Swing UI updates must happen on the Event Dispatch Thread; for a demo
        // this direct append is acceptable since all interactions are EDT-driven.
        logArea.append(msg);
    }
}
