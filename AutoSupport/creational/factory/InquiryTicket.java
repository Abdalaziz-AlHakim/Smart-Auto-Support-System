package creational.factory;


/**
 * Concrete ticket representing a general inquiry or question.
 *
 * <b>Design Pattern:</b> Factory Method (Concrete Product)
 */
public class InquiryTicket extends Ticket {

    /**
     * Constructs a new InquiryTicket.
     *
     * @param id          unique ticket ID
     * @param title       short summary of the inquiry
     * @param description detailed description of the inquiry
     */
    public InquiryTicket(int id, String title, String description) {
        super(id, title, description);
        this.type = TicketType.INQUIRY;
    }

    /**
     * Returns the human-readable type label for this ticket.
     *
     * @return "Inquiry"
     */
    @Override
    public String getTypeLabel() {
        return "Inquiry";
    }
}
