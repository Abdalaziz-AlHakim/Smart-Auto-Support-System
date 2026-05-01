package structural.facade;

import creational.factory.TicketType;
import creational.factory.TicketStatus;
import creational.factory.Ticket;
import creational.factory.TicketFactory;
import creational.singleton.TicketSystem;
import structural.adapter.EmailMessage;
import structural.adapter.EmailTicketAdapter;
import structural.adapter.TicketSource;
import structural.decorator.UrgentTicketDecorator;

import java.util.List;

/**
 * Facade that hides the complexity of ticket creation, decoration, registration,
 * and logging behind simple method calls.
 * <p>
 * Clients call {@link #submitTicket} or {@link #submitFromEmail} without needing
 * to know about factories, decorators, adapters, or the ticket system internals.
 * </p>
 *
 * <b>Design Pattern:</b> Facade
 */
public class SupportFacade {

    /** Reference to the global ticket system singleton. */
    private TicketSystem system = TicketSystem.getInstance();

    /** Shared log list — all messages appear in the GUI log panel. */
    private List<String> log;

    /**
     * Constructs a SupportFacade with the given shared log.
     *
     * @param sharedLog the shared log list for recording actions
     */
    public SupportFacade(List<String> sharedLog) {
        this.log = sharedLog;
    }

    /**
     * Full ticket submission pipeline in one call:
     * <ol>
     *   <li>Create ticket via Factory</li>
     *   <li>Optionally wrap with UrgentDecorator</li>
     *   <li>Register in TicketSystem</li>
     *   <li>Log the action</li>
     * </ol>
     *
     * @param type        the ticket category
     * @param title       short summary
     * @param description detailed description
     * @param urgent      whether to mark as urgent
     * @return the created (and possibly decorated) ticket
     */
    public Ticket submitTicket(TicketType type, String title, String description, boolean urgent) {
        Ticket ticket = TicketFactory.createTicket(type, title, description);
        log.add("> Ticket #" + ticket.getId() + " created as " + ticket.getTypeLabel());

        if (urgent) {
            ticket = new UrgentTicketDecorator(ticket);
            log.add("> Ticket #" + ticket.getId() + " decorated as URGENT");
        }

        ticket.setStatus(TicketStatus.IN_PROGRESS);
        system.addTicket(ticket);
        log.add("> Ticket #" + ticket.getId() + " registered in system. Status: IN_PROGRESS");
        return ticket;
    }

    /**
     * Submit a ticket sourced from a simulated email (uses Adapter internally).
     *
     * @param email  the email message to convert
     * @param urgent whether to mark as urgent
     * @return the created (and possibly decorated) ticket
     */
    public Ticket submitFromEmail(EmailMessage email, boolean urgent) {
        TicketSource adapter = new EmailTicketAdapter(email);
        Ticket ticket = adapter.toTicket();
        log.add("> Email from [" + email.getSender() + "] adapted into Ticket #" + ticket.getId());

        if (urgent) {
            ticket = new UrgentTicketDecorator(ticket);
            log.add("> Ticket #" + ticket.getId() + " decorated as URGENT");
        }

        ticket.setStatus(TicketStatus.IN_PROGRESS);
        system.addTicket(ticket);
        log.add("> Ticket #" + ticket.getId() + " registered from email. Status: IN_PROGRESS");
        return ticket;
    }
}
