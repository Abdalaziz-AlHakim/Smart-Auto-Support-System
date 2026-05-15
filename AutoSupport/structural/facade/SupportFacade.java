package structural.facade;

import behavioral.state.TicketContext;
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

        // Ticket starts in OPEN state (awaiting pickup)
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

        // Ticket starts in OPEN state
        TicketSystem.getInstance().addTicket(ticket);
        return ticket;
    }

    public void markAsUrgent(Ticket ticket) {
        if (ticket.getPriority() != creational.factory.Priority.URGENT) {
            Ticket urgentTicket = new UrgentTicketDecorator(ticket);
            
            TicketSystem.getInstance().updateTicket(urgentTicket);
            
            // Fire event to update UI
            TicketSystem.getInstance().notifyListeners(
                new behavioral.observer.TicketEvent(urgentTicket, behavioral.observer.TicketEventType.ESCALATED));
        }
    }

    /**
     * Move a ticket to the ESCALATED state and run a routing strategy.
     * 
     * Flow:
     *  1. Advance status via State pattern (InProgress -> Escalated).
     *  2. Build handler chain via Strategy pattern.
     *  3. Execute chain via Chain of Responsibility.
     *  4. Broadcast event via Observer.
     */
    public void escalateTicket(Ticket ticket, behavioral.strategy.RoutingStrategy strategy, List<String> log) {
        // 1 — State Pattern: Change status to ESCALATED
        // Use fromExisting() to ensure we start in the correct State object
        TicketContext context = TicketContext.fromExisting(ticket);
        
        // If ticket is still OPEN, pick it up first (OPEN -> IN_PROGRESS)
        if (ticket.getStatus() == TicketStatus.OPEN) {
            context.startProgress();
        }
        
        context.escalate(); // Moves status to ESCALATED

        // 2 — Strategy & Chain: Route the ticket
        // Strategy builds the chain; we then call handle() on the head
        behavioral.chain.SupportHandler head = strategy.buildChain(ticket, log);
        head.handle(ticket, log);

        // 3 — Observer: Notify system of the change
        TicketSystem.getInstance().notifyListeners(
            new behavioral.observer.TicketEvent(ticket, behavioral.observer.TicketEventType.ESCALATED)
        );
    }
}
