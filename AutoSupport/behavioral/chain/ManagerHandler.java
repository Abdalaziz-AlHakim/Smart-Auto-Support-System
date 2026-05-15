package behavioral.chain;

import behavioral.observer.TicketEvent;
import behavioral.observer.TicketEventType;
import creational.factory.Ticket;
import creational.factory.TicketStatus;
import creational.singleton.TicketSystem;

import java.util.List;

/**
 * Final handler in the escalation chain — resolves everything that reaches it.
 *
 * PATTERN: Chain of Responsibility (Concrete Handler — Manager / Final fallback)
 * ManagerHandler never calls passToNext() because it is always the last link.
 * No ticket should leave the chain unresolved — this handler guarantees that.
 */
public class ManagerHandler extends SupportHandler {

    /**
     * Resolve any ticket that reaches this level.
     * ManagerHandler is the authority of last resort — it handles all ticket types.
     *
     * @param ticket The ticket to process.
     * @param log    Mutable log list — this handler appends its decision.
     */
    @Override
    public void handle(Ticket ticket, List<String> log) {
        log.add("✅ Manager: Accepted " + ticket.getType()
                + " ticket #" + ticket.getId() + " for review (final authority).");
    }
}
