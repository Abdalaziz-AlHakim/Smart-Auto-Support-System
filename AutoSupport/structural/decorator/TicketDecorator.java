package structural.decorator;

import creational.factory.Priority;
import creational.factory.Ticket;
import creational.factory.TicketStatus;
import creational.factory.TicketType;

/**
 * Abstract base for all ticket decorators.
 *
 * PATTERN: Decorator
 * By extending Ticket AND holding a Ticket reference, this class allows any
 * concrete decorator to wrap any Ticket (including other decorators) while
 * still being treated as a Ticket throughout the system.
 *
 * Default behaviour: all method calls delegate to the wrapped ticket,
 * so concrete decorators only need to override the methods they change.
 */
public abstract class TicketDecorator extends Ticket {

    /** The ticket being wrapped. May itself be a decorator (stacking is supported). */
    protected final Ticket wrapped;

    /**
     * @param wrapped The ticket to decorate. Must not be null.
     */
    public TicketDecorator(Ticket wrapped) {
        // Pass the wrapped ticket's data to Ticket's constructor so this object
        // has its own copy of id/title/desc — but all calls are delegated below.
        super(wrapped.getId(), wrapped.getTitle(), wrapped.getDescription());
        this.wrapped = wrapped;
    }

    // Default delegation (concrete decorators override only what they change)

    @Override public TicketType getType()        { return wrapped.getType(); }
    @Override public String getTypeLabel()        { return wrapped.getTypeLabel(); }
    @Override public Priority getPriority()       { return wrapped.getPriority(); }
    @Override public TicketStatus getStatus()     { return wrapped.getStatus(); }
    @Override public void setStatus(TicketStatus s) { wrapped.setStatus(s); }
    @Override public String toString()            { return wrapped.toString(); }
}
