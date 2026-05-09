package behavioral.strategy;

import behavioral.chain.Level1Handler;
import behavioral.chain.Level2Handler;
import behavioral.chain.ManagerHandler;
import behavioral.chain.SupportHandler;
import creational.factory.Ticket;

import java.util.List;

/**
 * Standard routing: assembles the full L1 → L2 → Manager chain.
 *
 * PATTERN: Strategy (Concrete Strategy)
 * Used for normal-priority tickets where no special routing is needed.
 * L1 gets the first chance; if it cannot resolve, L2 tries; Manager is final fallback.
 */
public class StandardRoutingStrategy implements RoutingStrategy {

    /**
     * Build and link: L1 → L2 → Manager, then return L1 as chain head.
     */
    @Override
    public SupportHandler buildChain(Ticket ticket, List<String> log) {
        log.add("Strategy: Standard routing (L1 → L2 → Manager)");

        Level1Handler  l1  = new Level1Handler();
        Level2Handler  l2  = new Level2Handler();
        ManagerHandler mgr = new ManagerHandler();

        // Wire the chain using the fluent setNext() calls from SupportHandler
        l1.setNext(l2).setNext(mgr);

        return l1; // Caller passes this head directly to chain.handle(ticket, log)
    }
}
