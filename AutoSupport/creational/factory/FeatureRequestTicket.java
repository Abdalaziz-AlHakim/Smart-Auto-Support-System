package creational.factory;

/**
 * Concrete ticket representing a user-requested new feature or enhancement.
 *
 * PATTERN: Factory Method — concrete Product.
 * Created by TicketFactory when TicketType.FEATURE_REQUEST is requested.
 * Level2Handler handles FEATURE_REQUEST tickets.
 */
public class FeatureRequestTicket extends Ticket {

    public FeatureRequestTicket(int id, String title, String description) {
        super(id, title, description);
    }

    @Override
    public TicketType getType() { return TicketType.FEATURE_REQUEST; }

    @Override
    public String getTypeLabel() { return "Feature Request"; }
}
