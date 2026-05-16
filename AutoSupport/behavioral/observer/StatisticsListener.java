package behavioral.observer;

import javax.swing.JLabel;
// Updated by Fady
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

    @Override
    public void onTicketEvent(TicketEvent event) {
        // Derive counts from the actual ticket list — immune to counter drift
        // or logic errors in manual increment/decrement.
        recomputeStats();
        refreshLabel(); // Re-render the status bar text
    }

    /** Re-derive all totals by scanning the global ticket registry. */
    private void recomputeStats() {
        openCount = 0;
        escalatedCount = 0;
        resolvedCount = 0;

        for (creational.factory.Ticket t : creational.singleton.TicketSystem.getInstance().getAllTickets()) {
            switch (t.getStatus()) {
                case OPEN:
                case IN_PROGRESS:
                    openCount++;
                    break;
                case ESCALATED:
                    escalatedCount++;
                    break;
                case RESOLVED:
                    resolvedCount++;
                    break;
            }
            
            if (t.getPriority() == creational.factory.Priority.URGENT && t.getStatus() != creational.factory.TicketStatus.ESCALATED) {
                escalatedCount++;
            }
        }
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
