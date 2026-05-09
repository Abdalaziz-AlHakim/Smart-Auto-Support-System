package creational.factory;

/**
 * Enum representing the possible lifecycle statuses of a ticket.
 *
 * Managed by the State pattern (TicketContext + state classes).
 * The State pattern guarantees only legal transitions between these values occur.
 */
public enum TicketStatus {
    OPEN,               // Ticket just submitted, awaiting agent pickup
    IN_PROGRESS,        // Agent is actively working on the ticket
    ESCALATED,          // Ticket passed beyond L1, handled by L2 or Manager
    RESOLVED            // Ticket has been closed / solution confirmed
}
