package creational.factory;

/**
 * Concrete ticket representing a software defect (bug report).
 *
 * PATTERN: Factory Method — this is one of the concrete Products.
 * TicketFactory creates this when TicketType.BUG is requested.
 * Level2Handler is responsible for resolving BUG tickets.
 */
public class BugTicket extends Ticket {

    public BugTicket(int id, String title, String description) {
        super(id, title, description);
    }

    @Override
    public TicketType getType() { return TicketType.BUG; }

    @Override
    public String getTypeLabel() { return "Bug"; }
}
