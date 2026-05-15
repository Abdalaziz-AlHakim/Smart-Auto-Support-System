package behavioral.state;

import creational.factory.TicketStatus;

/**
 * State: OPEN — the ticket has just been submitted and awaits agent pickup.
 *
 * PATTERN: State (Concrete State)
 * Legal transition FROM this state: startProgress() → InProgressState
 * All other transitions are illegal and throw IllegalStateException.
 */
public class OpenState implements TicketState {

    @Override
    public void startProgress(TicketContext ctx) {
        // Valid transition: agent picks up the ticket
        ctx.getTicket().setStatus(TicketStatus.IN_PROGRESS);
        ctx.setState(new InProgressState()); // Move context to the next state
    }

    @Override
    public void escalate(TicketContext ctx) {
        // Cannot escalate a ticket that hasn't been picked up yet
        throw new IllegalStateException(
            "Cannot escalate an OPEN ticket. Call startProgress() first.");
    }

    @Override
    public void resolve(TicketContext ctx) {
        ctx.getTicket().setStatus(TicketStatus.RESOLVED);
        ctx.setState(new ResolvedState());
    }

    @Override
    public void reopen(TicketContext ctx) {
        // Ticket is already open — reopening is a no-op or an error
        throw new IllegalStateException("Ticket is already OPEN.");
    }

    @Override
    public String getName() { return "OPEN"; }
}
