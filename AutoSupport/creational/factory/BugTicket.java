package creational.factory;


/**
 * Concrete ticket representing a software bug or defect report.
 *
 * <b>Design Pattern:</b> Factory Method (Concrete Product)
 */
public class BugTicket extends Ticket {

    /**
     * Constructs a new BugTicket.
     *
     * @param id          unique ticket ID
     * @param title       short summary of the bug
     * @param description detailed description of the bug
     */
    public BugTicket(int id, String title, String description) {
        super(id, title, description);
        this.type = TicketType.BUG;
    }

    /**
     * Returns the human-readable type label for this ticket.
     *
     * @return "Bug"
     */
    @Override
    public String getTypeLabel() {
        return "Bug";
    }
}
