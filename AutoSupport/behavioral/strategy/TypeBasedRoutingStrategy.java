package behavioral.strategy;

import behavioral.chain.*;
import creational.factory.Ticket;
import creational.factory.TicketType;

import java.util.List;

/**
 * Type-based routing: sends each ticket type directly to its best handler.
 *
 * PATTERN: Strategy (Concrete Strategy)
 * Instead of always starting from L1, this strategy inspects the ticket type
 * and builds a chain starting from the handler most qualified to resolve it:
 *
 *   INQUIRY               → L1 (simple question; L1 handles it)
 *   BUG / FEATURE_REQUEST → L2 (technical; L1 is skipped)
 *   COMPLAINT             → Manager (only manager closes complaints; L1+L2 skipped)
 *
 * This is the most efficient strategy when ticket type is known in advance —
 * it avoids unnecessary handler passes and reduces resolution time.
 */
public class TypeBasedRoutingStrategy implements RoutingStrategy {

    /**
     * Build a type-aware chain starting at the handler best suited for this ticket.
     *
     *   INQUIRY               → L1 → L2 → Manager (normal flow)
     *   BUG / FEATURE_REQUEST → L2 → Manager       (L1 skipped)
     *   COMPLAINT             → Manager             (only manager handles complaints)
     *
     * @param ticket The ticket being escalated; its type determines the chain head.
     * @param log    Mutable log list; the strategy records its routing decision here.
     * @return       The head of the handler chain appropriate for this ticket type.
     */
    @Override
    public SupportHandler buildChain(Ticket ticket, List<String> log) {
        Level1Handler  l1  = new Level1Handler();
        Level2Handler  l2  = new Level2Handler();
        ManagerHandler mgr = new ManagerHandler();

        TicketType type = ticket.getType();

        switch (type) {
            case INQUIRY:
                // INQUIRY: start at L1 — normal support question
                log.add("Strategy: Type-Based → INQUIRY routed to L1.");
                l1.setNext(l2).setNext(mgr);
                return l1;

            case BUG:
            case FEATURE_REQUEST:
                // BUG / FEATURE_REQUEST: start at L2 — skip L1 (not technically capable)
                log.add("Strategy: Type-Based → " + type + " routed to L2 (L1 skipped).");
                l2.setNext(mgr);
                return l2;

            case COMPLAINT:
                // COMPLAINT: start at Manager — only manager can close complaints
                log.add("Strategy: Type-Based → COMPLAINT routed directly to Manager.");
                return mgr;

            default:
                // Fallback to full chain for any future ticket types
                log.add("Strategy: Type-Based → unknown type; defaulting to full chain.");
                l1.setNext(l2).setNext(mgr);
                return l1;
        }
    }
}

