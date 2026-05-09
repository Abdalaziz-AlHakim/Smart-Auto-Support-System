package behavioral.state;

import creational.factory.TicketStatus;

/**
 * State: IN_PROGRESS — an agent is actively working on the ticket.
 *
 * PATTERN: State (Concrete State)
 * Legal transitions FROM this state:
 *   escalate() → EscalatedState   (agent cannot handle it alone)
 *   resolve()  → ResolvedState    (agent resolved it at this level)
 */
public class InProgressState implements TicketState {

    @Override
    public void startProgress(TicketContext ctx) {
        // Already in progress — calling this again makes no sense
        throw new IllegalStateException("Ticket is already IN_PROGRESS.");
    }

    @Override
    public void escalate(TicketContext ctx) {
        // Agent determined the ticket needs higher-level handling
        ctx.getTicket().setStatus(TicketStatus.ESCALATED);
        ctx.setState(new EscalatedState()); // Advance context to escalated state
    }

    @Override
    public void resolve(TicketContext ctx) {
        // Agent resolved the ticket without needing escalation
        ctx.getTicket().setStatus(TicketStatus.RESOLVED);
        ctx.setState(new ResolvedState()); // Mark as done
    }

    @Override
    public void reopen(TicketContext ctx) {
        // A ticket in progress cannot be reopened — it isn't closed yet
        throw new IllegalStateException(
            "Cannot reopen an IN_PROGRESS ticket. Resolve it first.");
    }

    @Override
    public String getName() { return "IN_PROGRESS"; }
}
