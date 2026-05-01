package behavioral.chain;

import creational.factory.Ticket;
import creational.factory.TicketType;
import creational.factory.TicketStatus;

import java.util.List;

/**
 * Level 1 support handler. Resolves {@link TicketType#INQUIRY} tickets;
 * escalates all other types to the next handler in the chain.
 *
 * <b>Design Pattern:</b> Chain of Responsibility (Concrete Handler)
 */
public class Level1Handler extends SupportHandler {

    /**
     * Constructs a Level1Handler with the given shared log.
     *
     * @param log the shared log list
     */
    public Level1Handler(List<String> log) {
        super(log);
    }

    /**
     * Handles the ticket. Resolves inquiries; escalates everything else to L2.
     *
     * @param ticket the ticket to handle
     */
    @Override
    public void handle(Ticket ticket) {
        ticket.setStatus(TicketStatus.ESCALATED_L1);
        log.add("> L1 Agent handling Ticket #" + ticket.getId() + " (" + ticket.getTypeLabel() + ")");
        if (ticket.getType() == TicketType.INQUIRY) {
            ticket.setStatus(TicketStatus.RESOLVED);
            log.add("> L1 Agent RESOLVED Ticket #" + ticket.getId());
        } else {
            log.add("> L1 cannot resolve. Escalating to L2...");
            ticket.setStatus(TicketStatus.ESCALATED_L2);
            escalate(ticket);
        }
    }
}
