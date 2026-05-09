package creational.factory;

/**
 * Concrete ticket representing a general information request or question.
 *
 * PATTERN: Factory Method — concrete Product.
 * Created by TicketFactory when TicketType.INQUIRY is requested.
 * Level1Handler can resolve INQUIRY tickets without escalation.
 */
public class InquiryTicket extends Ticket {

    public InquiryTicket(int id, String title, String description) {
        super(id, title, description);
    }

    @Override
    public TicketType getType() { return TicketType.INQUIRY; }

    @Override
    public String getTypeLabel() { return "Inquiry"; }
}
