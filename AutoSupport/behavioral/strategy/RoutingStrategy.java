package behavioral.strategy;

import behavioral.chain.SupportHandler;
import creational.factory.Ticket;
import java.util.List;

/**
 * Strategy interface for assembling an escalation handler chain.
 *
 * PATTERN: Strategy
 * Each implementation decides which handlers to include and in what order,
 * then wires them together and returns the head of the chain.
 * SupportFacade calls buildChain() at escalation time, so the routing
 * algorithm can be swapped at runtime (e.g., from a GUI dropdown) without
 * touching any chain or facade logic.
 *
 * The ticket reference is passed so type-aware strategies (TypeBasedRoutingStrategy)
 * can inspect the ticket and build an appropriately targeted chain.
 */
public interface RoutingStrategy {

    /**
     * Build and return the head of the handler chain to use for escalation.
     *
     * @param ticket The ticket being escalated; may be used by the strategy
     *               to choose the optimal starting handler.
     * @param log    A mutable list each handler appends its decision message to;
     *               the GUI displays this log to the agent after escalation.
     * @return       The first SupportHandler in the assembled chain.
     */
    SupportHandler buildChain(Ticket ticket, List<String> log);
}
