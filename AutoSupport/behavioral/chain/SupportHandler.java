package behavioral.chain;

import creational.factory.Ticket;
import java.util.List;

/**
 * Abstract base for all escalation handlers in the Chain of Responsibility.
 *
 * PATTERN: Chain of Responsibility
 * Each handler keeps a reference to the NEXT handler in the chain.
 * If a handler cannot resolve the ticket, it calls next.handle() to pass it
 * along. ManagerHandler (the final link) has no next — it resolves everything.
 *
 * RoutingStrategy builds the chain by linking handlers with setNext().
 */
public abstract class SupportHandler {

    /** The next handler in the chain; null only for the final (Manager) handler. */
    private SupportHandler next;

    /**
     * Wire the next handler in the chain.
     * Returns 'next' so setNext() calls can be chained fluently:
     *   l1.setNext(l2).setNext(manager)
     *
     * @param next The handler to delegate to when this one cannot resolve.
     * @return     The same 'next' reference (for fluent chaining).
     */
    public SupportHandler setNext(SupportHandler next) {
        this.next = next;
        return next; // enables fluent: l1.setNext(l2).setNext(mgr)
    }

    /**
     * Try to handle the ticket; if unable, pass it to the next handler.
     *
     * @param ticket The ticket being escalated.
     * @param log    Mutable list where each handler records its decision.
     */
    public abstract void handle(Ticket ticket, List<String> log);

    /**
     * Helper for subclasses: pass the ticket to the next handler,
     * or log a "no handler" message if this is the end of the chain.
     */
    protected void passToNext(Ticket ticket, List<String> log) {
        if (next != null) {
            next.handle(ticket, log);
        } else {
            // This should never happen if ManagerHandler is always the final link
            log.add("⚠ No handler could process ticket #" + ticket.getId());
        }
    }
}
