package behavioral.chain;

import creational.factory.Ticket;
import creational.factory.TicketStatus;

import java.util.List;

/**
 * Manager-level support handler. This is the final handler in the chain
 * and resolves all remaining tickets regardless of type.
 *
 * <b>Design Pattern:</b> Chain of Responsibility (Concrete Handler)
 */
public class ManagerHandler extends SupportHandler {

    /**
     * Constructs a ManagerHandler with the given shared log.
     *
     * @param log the shared log list
     */
    public ManagerHandler(List<String> log) {
        super(log);
    }

    /**
     * Handles the ticket by resolving it unconditionally.
     * The manager is the last resort in the escalation chain.
     *
     * @param ticket the ticket to handle
     */
    @Override
    public void handle(Ticket ticket) {
        log.add("> Manager handling Ticket #" + ticket.getId());
        ticket.setStatus(TicketStatus.RESOLVED);
        log.add("> Manager RESOLVED Ticket #" + ticket.getId());
    }
}
