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
            ticket.setStatus(TicketStatus.RESOLVED);
            log.add("✅ L2 Agent: RESOLVED " + ticket.getType()
                    + " ticket #" + ticket.getId());
            TicketSystem.getInstance().notifyListeners(
                new TicketEvent(ticket, TicketEventType.RESOLVED));
        } else {
            log.add("➡ L2 Agent: Cannot handle " + ticket.getType()
                    + " — passing to Manager.");
            passToNext(ticket, log);
        }
    }
}
