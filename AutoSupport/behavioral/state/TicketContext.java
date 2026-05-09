package behavioral.state;

import creational.factory.Ticket;
import creational.factory.TicketStatus;

/**
 * Context object that wraps a Ticket and delegates lifecycle actions to the current state.
 *
 * PATTERN: State (Context)
 * BUG FIX: Added fromExisting() factory so SupportFacade can restore the correct
 * state when operating on a ticket that was already partially processed,
 * rather than always starting in OpenState.
 */
public class TicketContext {

    private final Ticket ticket;
    private TicketState currentState;

    /** New ticket — always starts in OpenState. */
    public TicketContext(Ticket ticket) {
        this.ticket       = ticket;
        this.currentState = new OpenState();
    }

    /**
     * Restore a context for an EXISTING ticket by mapping its current status
     * to the corresponding State object.
     * Without this, creating a TicketContext for an IN_PROGRESS ticket would
     * incorrectly start in OpenState and throw on escalate()/resolve().
     */
    public static TicketContext fromExisting(Ticket ticket) {
        TicketContext ctx = new TicketContext(ticket);
        switch (ticket.getStatus()) {
            case IN_PROGRESS: ctx.currentState = new InProgressState(); break;
            case ESCALATED:   ctx.currentState = new EscalatedState();  break;
            case RESOLVED:    ctx.currentState = new ResolvedState();   break;
            default:          /* OPEN — already set by constructor */    break;
        }
        return ctx;
    }

    public void startProgress() { currentState.startProgress(this); }
    public void escalate()      { currentState.escalate(this); }
    public void resolve()       { currentState.resolve(this); }
    public void reopen()        { currentState.reopen(this); }

    /** Called only by TicketState implementations to advance the machine. */
    public void setState(TicketState newState) { this.currentState = newState; }

    public Ticket getTicket()     { return ticket; }
    public String getStateName()  { return currentState.getName(); }
}
