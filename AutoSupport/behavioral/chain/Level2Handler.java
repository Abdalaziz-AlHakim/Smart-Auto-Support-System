package behavioral.chain;

import behavioral.observer.TicketEvent;
import behavioral.observer.TicketEventType;
import creational.factory.Ticket;
import creational.factory.TicketStatus;
import creational.factory.TicketType;
import creational.singleton.TicketSystem;

import java.util.List;

/**
 * Second handler — resolves BUG and FEATURE_REQUEST; passes COMPLAINT up.
 *
 * BUG FIX: Removed redundant ESCALATED observer event from the pass-along path.
 */
public class Level2Handler extends SupportHandler {

    @Override
    public void handle(Ticket ticket, List<String> log) {
        if (ticket.getType() == TicketType.BUG
                || ticket.getType() == TicketType.FEATURE_REQUEST) {
            log.add("✅ L2 Agent: Accepted " + ticket.getType()
                    + " ticket #" + ticket.getId() + " for review.");
        } else {
            log.add("➡ L2 Agent: Cannot handle " + ticket.getType()
                    + " — passing to Manager.");
            passToNext(ticket, log);
        }
    }
}
