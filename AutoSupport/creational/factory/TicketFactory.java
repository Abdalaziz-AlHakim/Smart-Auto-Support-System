package creational.factory;

import creational.singleton.TicketSystem;

/**
 * Factory that creates the correct Ticket subclass based on TicketType.
 *
 * PATTERN: Factory Method
 * All ticket creation flows through this single class. Callers (SupportFacade,
 * EmailTicketAdapter) only specify WHAT kind of ticket they need — never HOW
 * to construct it. This centralises ID assignment and type mapping in one
 * place.
 *
 * Why not a simple constructor?
 * Direct 'new BugTicket(...)' calls scatter type-selection logic across the
 * codebase and would require callers to import every Ticket subclass.
 */
public class TicketFactory {

    /**
     * Create and return a typed Ticket subclass.
     *
     * ID is obtained from the TicketSystem Singleton so every ticket in the
     * system gets a globally unique, auto-incrementing identifier.
     *
     * @param type        The category of ticket to create
     * @param title       Short issue summary (shown in GUI table)
     * @param description Full problem details
     * @return A fully initialised Ticket subclass with OPEN status
     * @throws IllegalArgumentException if an unknown TicketType is passed
     */
    public static Ticket createTicket(TicketType type, String title, String description) {
        // Get the next unique ID from the global Singleton registry
        int id = TicketSystem.getInstance().generateId();

        switch (type) {
            case BUG:
                return new BugTicket(id, title, description);
            case COMPLAINT:
                return new ComplaintTicket(id, title, description);
            case FEATURE_REQUEST:
                return new FeatureRequestTicket(id, title, description);
            case INQUIRY:
                return new InquiryTicket(id, title, description);
            default:
                throw new IllegalArgumentException("Unknown ticket type: " + type);
        }
    }
}
