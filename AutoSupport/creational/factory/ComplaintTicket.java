package creational.factory;


/**
 * Concrete ticket representing a customer complaint.
 *
 * <b>Design Pattern:</b> Factory Method (Concrete Product)
 */
public class ComplaintTicket extends Ticket {

    /**
     * Constructs a new ComplaintTicket.
     *
     * @param id          unique ticket ID
     * @param title       short summary of the complaint
     * @param description detailed description of the complaint
     */
    public ComplaintTicket(int id, String title, String description) {
        super(id, title, description);
        this.type = TicketType.COMPLAINT;
    }

    /**
     * Returns the human-readable type label for this ticket.
     *
     * @return "Complaint"
     */
    @Override
    public String getTypeLabel() {
        return "Complaint";
    }
}
