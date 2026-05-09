package behavioral.state;

import creational.factory.TicketStatus;

/**
 * State: ESCALATED — the ticket has been passed to a higher authority.
 *
 * PATTERN: State (Concrete State)
 * Legal transitions FROM this state:
 *   resolve() → ResolvedState   (manager/L2 closes the ticket)
 * All others are illegal.
 */
public class EscalatedState implements TicketState {

    @Override
    public void startProgress(TicketContext ctx) {
        throw new IllegalStateException(
            "Cannot start progress on an already ESCALATED ticket.");
    }

    @Override
    public void escalate(TicketContext ctx) {
        // Already escalated — escalating again doesn't make sense in this model
        throw new IllegalStateException("Ticket is already ESCALATED.");
    }

    @Override
    public void resolve(TicketContext ctx) {
        // Authorised handler (MANAGER/ADMIN via Proxy) closes the ticket
        ctx.getTicket().setStatus(TicketStatus.RESOLVED);
        ctx.setState(new ResolvedState()); // Transition to final state
    }

    @Override
    public void reopen(TicketContext ctx) {
        // An escalated ticket must be resolved before it can be reopened
        throw new IllegalStateException(
            "Cannot reopen an ESCALATED ticket. Resolve it first.");
    }

    @Override
    public String getName() { return "ESCALATED"; }
}
