package behavioral.observer;

import javax.swing.JLabel;

/**
 * Observer that maintains live ticket statistics displayed in the GUI status bar.
 *
 * PATTERN: Observer (Concrete Observer)
 * Keeps three running counters: open, escalated, and resolved.
 * Each time a lifecycle event fires, the appropriate counter is updated
 * and the GUI JLabel is refreshed — without TicketSystem knowing the GUI exists.
 */
public class StatisticsListener implements TicketEventListener {

    /** GUI label where the stats summary is displayed (e.g., in a status bar). */
    private final JLabel statsLabel;

    // Running counters — incremented / decremented on events
    private int openCount      = 0;
    private int escalatedCount = 0;
    private int resolvedCount  = 0;

    /**
     * @param statsLabel The Swing label to update on every event. Must not be null.
     */
    public StatisticsListener(JLabel statsLabel) {
        this.statsLabel = statsLabel;
    }

    /**
     * Update the appropriate counter and refresh the GUI label.
     * Counter semantics:
     *   CREATED   → open +1
     *   ESCALATED → open -1, escalated +1
     *   RESOLVED  → escalated -1 (if was escalated) or open -1, resolved +1
     */
    @Override
    public void onTicketEvent(TicketEvent event) {
        switch (event.getType()) {

            case CREATED:
                openCount++;          // New ticket enters the open queue
                break;

            case ESCALATED:
                openCount--;          // Ticket leaves the open queue
                escalatedCount++;     // …and enters the escalated queue
                break;

            case RESOLVED:
                // Ticket could be coming from either open or escalated state
                if (escalatedCount > 0) {
                    escalatedCount--;
                } else {
                    openCount = Math.max(0, openCount - 1);
                }
                resolvedCount++;      // Always increment resolved
                break;

            case REOPENED:
                // Ticket moves from resolved back to open
                resolvedCount = Math.max(0, resolvedCount - 1);
                openCount++;
                break;
        }

        refreshLabel(); // Re-render the status bar text
    }

    /** Update the JLabel text with the current counter values. */
    private void refreshLabel() {
        statsLabel.setText(String.format(
            "  Open: %d   |   Escalated: %d   |   Resolved: %d  ",
            openCount, escalatedCount, resolvedCount));
    }

    // ── Accessors for unit testing ────────────────────────────────────────────
    public int getOpenCount()      { return openCount; }
    public int getEscalatedCount() { return escalatedCount; }
    public int getResolvedCount()  { return resolvedCount; }
}
