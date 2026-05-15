package behavioral.chain;

import behavioral.observer.TicketEvent;
import behavioral.observer.TicketEventType;
import creational.factory.Ticket;
import creational.factory.TicketStatus;
import creational.factory.TicketType;
import creational.singleton.TicketSystem;

import java.util.List;

/**
 * First handler — resolves INQUIRY tickets; passes everything else up.
 *
 * BUG FIX: Removed the redundant ESCALATED observer event from the pass-along
 * path. SupportFacade now fires ESCALATED once before running the chain.
 * Double-firing caused observers (StatisticsListener) to count incorrectly.
 */
public class Level1Handler extends SupportHandler {

    @Override
    public void handle(Ticket ticket, List<String> log) {
        if (ticket.getType() == TicketType.INQUIRY) {
            log.add("✅ L1 Agent: Accepted Inquiry ticket #" + ticket.getId() + " for review.");
        } else {
            // Cannot handle — pass to L2 (no extra ESCALATED event; Facade already fired it)
            log.add("➡ L1 Agent: Cannot handle " + ticket.getType() + " — passing to L2.");
            passToNext(ticket, log);
        }
    }
}
