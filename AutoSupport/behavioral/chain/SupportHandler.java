package behavioral.chain;

import creational.factory.Ticket;
import creational.factory.TicketStatus;

import java.util.List;

/**
 * Abstract handler in the Chain of Responsibility pattern.
 * <p>
 * Each handler either resolves the ticket or escalates it to the next handler
 * in the chain. This models real-world support tiers.
 * </p>
 *
 * <b>Design Pattern:</b> Chain of Responsibility (Abstract Handler)
 */
public abstract class SupportHandler {

    /** The next handler in the chain. */
    protected SupportHandler nextHandler;

    /** Shared log list for recording handling actions. */
    protected List<String> log;

    /**
     * Constructs a SupportHandler with the given shared log.
     *
     * @param log the shared log list
     */
    public SupportHandler(List<String> log) {
        this.log = log;
    }

    /**
     * Sets the next handler in the chain.
     *
     * @param next the next handler
     */
    public void setNext(SupportHandler next) {
        this.nextHandler = next;
    }

    /**
     * Attempts to handle the given ticket. If this handler cannot resolve it,
     * the ticket is passed to the next handler via {@link #escalate(Ticket)}.
     *
     * @param ticket the ticket to handle
     */
    public abstract void handle(Ticket ticket);

    /**
     * Escalates the ticket to the next handler in the chain.
     * If no next handler exists, the ticket is resolved as a fallback.
     *
     * @param ticket the ticket to escalate
     */
    protected void escalate(Ticket ticket) {
        if (nextHandler != null) {
            nextHandler.handle(ticket);
        } else {
            log.add("> No handler could resolve Ticket #" + ticket.getId());
            ticket.setStatus(TicketStatus.RESOLVED); // fallback
        }
    }
}
