package structural.adapter;

import creational.factory.TicketType;
import creational.factory.Ticket;
import creational.factory.TicketFactory;
import creational.singleton.TicketSystem;

/**
 * Adapter that converts an {@link EmailMessage} into a {@link Ticket}.
 * <p>
 * The email's subject is analyzed to infer the ticket type (Bug, Complaint,
 * Feature Request, or Inquiry). The email body and sender are combined into
 * the ticket description.
 * </p>
 *
 * <b>Design Pattern:</b> Adapter (Adapter)
 */
public class EmailTicketAdapter implements TicketSource {

    /** The email message being adapted. */
    private EmailMessage email;

    /**
     * Constructs a new EmailTicketAdapter for the given email.
     *
     * @param email the email message to adapt
     */
    public EmailTicketAdapter(EmailMessage email) {
        this.email = email;
    }

    /**
     * Converts the stored {@link EmailMessage} into a {@link Ticket}.
     * <p>
     * The ticket type is inferred from keywords in the email subject:
     * <ul>
     *   <li>"bug", "error", "crash" → BUG</li>
     *   <li>"complaint", "unhappy" → COMPLAINT</li>
     *   <li>"feature", "request" → FEATURE_REQUEST</li>
     *   <li>Otherwise → INQUIRY</li>
     * </ul>
     * </p>
     *
     * @return a new Ticket created via the TicketFactory
     */
    @Override
    public Ticket toTicket() {
        // Infer type from subject keywords, default to Inquiry
        TicketType type = TicketType.INQUIRY;
        String subject = email.getSubject().toLowerCase();
        if (subject.contains("bug") || subject.contains("error") || subject.contains("crash"))
            type = TicketType.BUG;
        else if (subject.contains("complaint") || subject.contains("unhappy"))
            type = TicketType.COMPLAINT;
        else if (subject.contains("feature") || subject.contains("request"))
            type = TicketType.FEATURE_REQUEST;

        String title = email.getSubject();
        String description = "From: " + email.getSender() + "\n" + email.getBody();

        return TicketFactory.createTicket(type, title, description);
    }
}
