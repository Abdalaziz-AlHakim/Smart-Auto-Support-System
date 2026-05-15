package structural.proxy;

import creational.factory.Ticket;
import java.util.List;

/**
 * CONTRACT shared by both the real TicketSystem and its Proxy.
 *
 * PATTERN: Proxy
 * The GUI and Facade always program to this interface, never to the concrete
 * TicketSystem. This allows TicketSystemProxy to intercept every call
 * and enforce role-based security without the caller knowing.
 */
public interface ITicketSystem {

    /**
     * Register a new ticket in the system.
     * The Proxy will throw SecurityException if the caller's role lacks permission.
     */
    void addTicket(Ticket t);

    /**
     * Retrieve all tickets currently held in the registry.
     * Read-only operation — all roles are permitted.
     */
    List<Ticket> getAllTickets();

    /**
     * Mark a ticket as resolved.
     * The Proxy restricts this to MANAGER / ADMIN for manager-level escalations.
     */
    void resolveTicket(Ticket t);
}
