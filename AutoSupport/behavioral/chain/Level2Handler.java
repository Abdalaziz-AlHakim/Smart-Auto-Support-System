package behavioral.chain;

import creational.factory.Ticket;
import creational.factory.TicketType;
import creational.factory.TicketStatus;

import java.util.List;

/**
 * Level 2 support handler. Resolves {@link TicketType#BUG} and
 * {@link TicketType#FEATURE_REQUEST} tickets; escalates all others to Manager.
 *
 * <b>Design Pattern:</b> Chain of Responsibility (Concrete Handler)
 */
public class Level2Handler extends SupportHandler {

    /**
     * Constructs a Level2Handler with the given shared log.
     *
     * @param log the shared log list
     */
    public Level2Handler(List<String> log) {
        super(log);
    }

    /**
     * Handles the ticket. Resolves Bug and Feature Request tickets;
     * escalates everything else to the Manager.
     *
     * @param ticket the ticket to handle
     */
    @Override
    public void handle(Ticket ticket) {
        log.add("> L2 Agent handling Ticket #" + ticket.getId());
        if (ticket.getType() == TicketType.BUG || ticket.getType() == TicketType.FEATURE_REQUEST) {
            ticket.setStatus(TicketStatus.RESOLVED);
            log.add("> L2 Agent RESOLVED Ticket #" + ticket.getId());
        } else {
            log.add("> L2 cannot resolve. Escalating to Manager...");
            ticket.setStatus(TicketStatus.ESCALATED_MANAGER);
            escalate(ticket);
        }
    }
}
