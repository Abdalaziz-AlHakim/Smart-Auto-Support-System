package creational.factory;

/**
 * Concrete ticket representing a user complaint about service or product quality.
 *
 * PATTERN: Factory Method — concrete Product.
 * Created by TicketFactory when TicketType.COMPLAINT is requested.
 * ManagerHandler is responsible for resolving COMPLAINT tickets.
 */
public class ComplaintTicket extends Ticket {

    public ComplaintTicket(int id, String title, String description) {
        super(id, title, description);
    }

    @Override
    public TicketType getType() { return TicketType.COMPLAINT; }

    @Override
    public String getTypeLabel() { return "Complaint"; }
}
