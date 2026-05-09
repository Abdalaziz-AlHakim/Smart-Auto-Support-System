package behavioral.state;

import creational.factory.TicketStatus;

/**
 * State: RESOLVED — the ticket has been fully closed.
 *
 * PATTERN: State (Concrete State)
 * Legal transitions FROM this state:
 *   reopen() → OpenState   (customer reports issue recurred)
 * All other transitions are illegal — you cannot escalate or re-resolve a closed ticket.
 */
public class ResolvedState implements TicketState {

    @Override
    public void startProgress(TicketContext ctx) {
        throw new IllegalStateException("Cannot start progress on a RESOLVED ticket.");
    }

    @Override
    public void escalate(TicketContext ctx) {
        throw new IllegalStateException("Cannot escalate a RESOLVED ticket.");
    }

    @Override
    public void resolve(TicketContext ctx) {
        throw new IllegalStateException("Ticket is already RESOLVED.");
    }

    @Override
    public void reopen(TicketContext ctx) {
        // Customer reports the issue recurred — put the ticket back into the queue
        ctx.getTicket().setStatus(TicketStatus.OPEN);
        ctx.setState(new OpenState()); // Cycle back to the beginning of the lifecycle
    }

    @Override
    public String getName() { return "RESOLVED"; }
}
