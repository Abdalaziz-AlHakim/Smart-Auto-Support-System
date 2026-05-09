package structural.facade;

import behavioral.chain.SupportHandler;
import behavioral.observer.TicketEvent;
import behavioral.observer.TicketEventType;
import behavioral.state.TicketContext;
import behavioral.strategy.RoutingStrategy;
import creational.factory.Ticket;
import creational.factory.TicketFactory;
import creational.factory.TicketStatus;
import creational.factory.TicketType;
import creational.singleton.TicketSystem;
import structural.adapter.EmailMessage;
import structural.adapter.EmailTicketAdapter;
import structural.decorator.UrgentTicketDecorator;

import java.util.List;

/**
 * Single entry point for all ticket submission and escalation operations.
 *
 * PATTERN: Facade
 * BUG FIXES applied here:
 *  1. escalateTicket() no longer creates a broken TicketContext from scratch.
 *     It now sets status + fires ESCALATED directly, then runs the chain.
 *  2. TicketContext.fromExisting() used for reopen so state is restored correctly.
 */
public class SupportFacade {

    /**
     * Full manual submission pipeline:
     *  Factory → (Decorator) → State[OPEN→IN_PROGRESS] → Singleton → Observer[CREATED]
     */
    public Ticket submitTicket(TicketType type, String title,
                               String description, boolean urgent) {
        Ticket ticket = TicketFactory.createTicket(type, title, description);

        if (urgent) {
            ticket = new UrgentTicketDecorator(ticket);
        }

        // Advance state: OPEN → IN_PROGRESS before registering
        TicketContext ctx = new TicketContext(ticket);
        ctx.startProgress();

        // Register triggers Observer CREATED event
        TicketSystem.getInstance().addTicket(ticket);
        return ticket;
    }

    /**
     * Email-sourced submission: Adapter detects type, then same pipeline.
     */
    public Ticket submitFromEmail(EmailMessage email, boolean urgent) {
        Ticket ticket = new EmailTicketAdapter(email).toTicket();

        if (urgent) {
            ticket = new UrgentTicketDecorator(ticket);
        }

        TicketContext ctx = new TicketContext(ticket);
        ctx.startProgress();
        TicketSystem.getInstance().addTicket(ticket);
        return ticket;
    }

    /**
     * Escalate a ticket through the chosen RoutingStrategy chain.
     *
     * PATTERN: State — transitions are enforced through TicketContext:
     *   • If OPEN → automatically advance to IN_PROGRESS first, then escalate.
     *   • If IN_PROGRESS → escalate directly.
     *   • Any other state (ESCALATED / RESOLVED) throws IllegalStateException.
     *
     * PATTERN: Strategy — the caller supplies a RoutingStrategy which builds the
     * handler chain; then PATTERN: Chain of Responsibility resolves the ticket.
     *
     * @param ticket   The ticket to escalate (must be OPEN or IN_PROGRESS).
     * @param strategy The routing strategy that determines the handler chain.
     * @param log      Mutable list each handler appends its decision to.
     * @throws IllegalStateException if the ticket cannot legally be escalated.
     */
    public void escalateTicket(Ticket ticket, RoutingStrategy strategy,
                               List<String> log) {
        TicketContext ctx = TicketContext.fromExisting(ticket);

        // Auto-advance OPEN → IN_PROGRESS (required by State machine before escalate)
        if (ticket.getStatus() == TicketStatus.OPEN) {
            ctx.startProgress();
        }
        // IN_PROGRESS → ESCALATED via the State machine
        ctx.escalate();

        // Notify observers that the ticket is now ESCALATED
        TicketSystem.getInstance().notifyListeners(
            new TicketEvent(ticket, TicketEventType.ESCALATED));

        // Strategy builds the chain; chain resolves the ticket
        SupportHandler chainHead = strategy.buildChain(ticket, log);
        chainHead.handle(ticket, log);
    }

    /**
     * Reopen a resolved ticket: RESOLVED → OPEN via the State machine.
     * Uses fromExisting() so the context starts in ResolvedState, not OpenState.
     *
     * Fires REOPENED (not CREATED) so stats listeners can correctly decrement
     * resolvedCount and increment openCount without double-counting.
     *
     * @throws IllegalStateException if the ticket is not currently RESOLVED.
     */
    public void reopenTicket(Ticket ticket) {
        TicketContext.fromExisting(ticket).reopen(); // RESOLVED → OPEN via State
        TicketSystem.getInstance().notifyListeners(
            new TicketEvent(ticket, TicketEventType.REOPENED));
    }
}
