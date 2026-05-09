package creational.factory;

/**
 * Enum representing the supported categories of support tickets.
 *
 * Used by TicketFactory to decide which Ticket subclass to instantiate,
 * and by the Chain of Responsibility handlers to decide which level resolves it.
 */
public enum TicketType {
    BUG,             // Software defect reported by a user
    COMPLAINT,       // User dissatisfaction with service or product
    FEATURE_REQUEST, // Request for new product functionality
    INQUIRY          // General question or information request
}
