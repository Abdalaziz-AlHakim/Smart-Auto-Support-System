package behavioral.observer;
// Updated by Fady
/**
 * Enum of ticket lifecycle events that the Observer system broadcasts.
 *
 * TicketSystem fires a TicketEvent carrying one of these values whenever
 * a meaningful state change occurs. Each registered TicketEventListener
 * decides what to do based on this type.
 */
public enum TicketEventType {
    CREATED,    // A new ticket was registered in the system
    ESCALATED,  // A ticket moved beyond its current handler level
    RESOLVED,   // A ticket was fully resolved and closed
    REOPENED,    // A previously RESOLVED ticket was reopened (back to OPEN)
    PICKED_UP   // An agent moved an OPEN ticket to IN_PROGRESS
}
