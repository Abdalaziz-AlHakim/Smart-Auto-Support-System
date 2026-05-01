package creational.singleton;

import creational.factory.Ticket;
import java.util.ArrayList;
import java.util.List;

/**
 * Singleton class that serves as the global registry for all support tickets.
 * <p>
 * Only one instance of {@code TicketSystem} exists at any time, ensuring a
 * centralized, consistent view of all tickets across the application.
 * </p>
 *
 * <b>Design Pattern:</b> Singleton
 */
public class TicketSystem {

    /** The single instance of the TicketSystem. */
    private static TicketSystem instance;

    /** Internal list holding all registered tickets. */
    private List<Ticket> tickets = new ArrayList<>();

    /** Auto-incrementing counter used to generate unique ticket IDs. */
    private int idCounter = 1;

    /**
     * Private constructor to prevent external instantiation.
     */
    private TicketSystem() {}

    /**
     * Returns the single instance of the TicketSystem, creating it if necessary.
     *
     * @return the global TicketSystem instance
     */
    public static TicketSystem getInstance() {
        if (instance == null) {
            instance = new TicketSystem();
        }
        return instance;
    }

    /**
     * Adds a ticket to the global registry.
     *
     * @param t the ticket to register
     */
    public void addTicket(Ticket t) {
        tickets.add(t);
    }

    /**
     * Returns the list of all registered tickets.
     *
     * @return list of all tickets
     */
    public List<Ticket> getAllTickets() {
        return tickets;
    }

    /**
     * Generates and returns the next unique ticket ID.
     *
     * @return a unique integer ID
     */
    public int generateId() {
        return idCounter++;
    }

    /**
     * Removes a ticket from the global registry.
     *
     * @param t the ticket to remove
     */
    public void removeTicket(Ticket t) {
        tickets.remove(t);
    }
}
