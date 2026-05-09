package behavioral.observer;

import creational.factory.Ticket;

/**
 * Immutable value object representing a single ticket lifecycle event.
 *
 * PATTERN: Observer
 * TicketSystem creates a TicketEvent each time a significant change occurs
 * (ticket created, escalated, or resolved) and passes it to all registered
 * TicketEventListeners via notifyListeners(). Listeners inspect the type
 * to decide how to react — none of them need to know about each other.
 */
public class TicketEvent {

    private final Ticket ticket;         // The ticket that triggered this event
    private final TicketEventType type;  // What kind of change occurred

    /**
     * @param ticket The affected ticket (never null)
     * @param type   The lifecycle event that occurred
     */
    public TicketEvent(Ticket ticket, TicketEventType type) {
        this.ticket = ticket;
        this.type   = type;
    }

    /** @return The ticket involved in this event. */
    public Ticket getTicket() { return ticket; }

    /** @return The type of lifecycle change (CREATED / ESCALATED / RESOLVED). */
    public TicketEventType getType() { return type; }

    @Override
    public String toString() {
        return String.format("EVENT[%s] → Ticket #%d (%s)",
                type, ticket.getId(), ticket.getTitle());
    }
}
