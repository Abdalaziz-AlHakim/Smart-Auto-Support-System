package creational.factory;

/**
 * Enumeration representing the lifecycle status of a support ticket.
 */
public enum TicketStatus {
    /** Ticket has been created but not yet processed. */
    OPEN,
    /** Ticket is actively being worked on. */
    IN_PROGRESS,
    /** Ticket has been escalated to Level 1 support. */
    ESCALATED_L1,
    /** Ticket has been escalated to Level 2 support. */
    ESCALATED_L2,
    /** Ticket has been escalated to the Manager level. */
    ESCALATED_MANAGER,
    /** Ticket has been resolved and closed. */
    RESOLVED
}
