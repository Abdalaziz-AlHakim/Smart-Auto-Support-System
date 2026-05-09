package structural.adapter;

import creational.factory.Ticket;

/**
 * Target interface that the AutoSupport system uses for any ticket source.
 *
 * PATTERN: Adapter
 * The system only knows about this interface. EmailTicketAdapter implements it
 * to bridge the gap between the external EmailMessage format and the Ticket
 * type the system expects — without modifying EmailMessage at all.
 */
public interface TicketSource {

    /**
     * Convert the underlying data source into a Ticket object
     * ready to be registered in TicketSystem.
     *
     * @return a fully constructed Ticket (subclass chosen by the adapter)
     */
    Ticket toTicket();
}
