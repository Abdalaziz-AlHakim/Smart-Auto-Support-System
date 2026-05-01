package creational.factory;


/**
 * Concrete ticket representing a feature request or enhancement suggestion.
 *
 * <b>Design Pattern:</b> Factory Method (Concrete Product)
 */
public class FeatureRequestTicket extends Ticket {

    /**
     * Constructs a new FeatureRequestTicket.
     *
     * @param id          unique ticket ID
     * @param title       short summary of the feature request
     * @param description detailed description of the feature request
     */
    public FeatureRequestTicket(int id, String title, String description) {
        super(id, title, description);
        this.type = TicketType.FEATURE_REQUEST;
    }

    /**
     * Returns the human-readable type label for this ticket.
     *
     * @return "Feature Request"
     */
    @Override
    public String getTypeLabel() {
        return "Feature Request";
    }
}
