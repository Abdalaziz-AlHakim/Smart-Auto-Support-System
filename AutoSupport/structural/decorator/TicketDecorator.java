package structural.decorator;

import creational.factory.Ticket;

/**
 * Abstract decorator that wraps a {@link Ticket} to add or modify behavior.
 * <p>
 * By extending {@code Ticket} and holding a reference to the wrapped ticket,
 * this class enables composable, runtime behavior additions without subclassing
 * every ticket type.
 * </p>
 *
 * <b>Design Pattern:</b> Decorator (Abstract Decorator)
 */
public abstract class TicketDecorator extends Ticket {

    /** The original ticket being decorated. */
    protected Ticket wrappedTicket;

    /**
     * Constructs a TicketDecorator that wraps the given ticket.
     * All fields are copied from the wrapped ticket.
     *
     * @param ticket the ticket to wrap
     */
    public TicketDecorator(Ticket ticket) {
        super(ticket.getId(), ticket.getTitle(), ticket.getDescription());
        this.wrappedTicket = ticket;
        this.type = ticket.getType();
        this.status = ticket.getStatus();
        this.priority = ticket.getPriority();
    }

    /**
     * Delegates the type label to the wrapped ticket.
     *
     * @return the wrapped ticket's type label
     */
    @Override
    public String getTypeLabel() {
        return wrappedTicket.getTypeLabel();
    }
}
