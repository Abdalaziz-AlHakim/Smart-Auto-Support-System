package structural.decorator;

import creational.factory.TicketPriority;
import creational.factory.Ticket;

/**
 * Concrete decorator that marks any {@link Ticket} as URGENT at runtime.
 * <p>
 * Adds urgent priority behavior and a visual prefix to the type label and
 * toString output, without subclassing every ticket type separately.
 * </p>
 *
 * <b>Design Pattern:</b> Decorator (Concrete Decorator)
 */
public class UrgentTicketDecorator extends TicketDecorator {

    /**
     * Wraps the given ticket and sets its priority to URGENT.
     *
     * @param ticket the ticket to decorate as urgent
     */
    public UrgentTicketDecorator(Ticket ticket) {
        super(ticket);
        this.priority = TicketPriority.URGENT;
    }

    /**
     * Returns the type label prefixed with "[URGENT]".
     *
     * @return the urgent-decorated type label
     */
    @Override
    public String getTypeLabel() {
        return "[URGENT] " + wrappedTicket.getTypeLabel();
    }

    /**
     * Returns a string representation prefixed with a red circle emoji.
     *
     * @return urgent-decorated string
     */
    @Override
    public String toString() {
        return "\uD83D\uDD34 " + super.toString();
    }
}
