package behavioral.strategy;

import behavioral.chain.Level2Handler;
import behavioral.chain.ManagerHandler;
import behavioral.chain.SupportHandler;
import creational.factory.Ticket;

import java.util.List;

/**
 * Urgent routing: skips Level 1 and starts directly at L2 → Manager.
 *
 * PATTERN: Strategy (Concrete Strategy)
 * Used when a ticket is marked URGENT (wrapped by UrgentTicketDecorator).
 * L1 agents handle simple inquiries — urgent issues deserve immediate
 * expert attention at L2, saving resolution time.
 */
public class UrgentRoutingStrategy implements RoutingStrategy {

    /**
     * Build L2 → Manager chain (L1 is deliberately excluded).
     */
    @Override
    public SupportHandler buildChain(Ticket ticket, List<String> log) {
        log.add("Strategy: Urgent routing (L2 → Manager, L1 skipped)");

        Level2Handler  l2  = new Level2Handler();
        ManagerHandler mgr = new ManagerHandler();

        l2.setNext(mgr); // Short chain — only two levels for urgent tickets

        return l2; // Start at L2
    }
}
