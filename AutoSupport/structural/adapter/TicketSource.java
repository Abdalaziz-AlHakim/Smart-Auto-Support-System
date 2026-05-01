package structural.adapter;

import creational.factory.Ticket;

/**
 * Target interface for the Adapter pattern.
 * <p>
 * Any source of tickets (e.g., email, API, manual input) must implement this
 * interface to produce a {@link Ticket} object that the system can work with.
 * </p>
 *
 * <b>Design Pattern:</b> Adapter (Target)
 */
public interface TicketSource {

    /**
     * Converts the source data into a {@link Ticket} object.
     *
     * @return a newly created Ticket
     */
    Ticket toTicket();
}
