package structural.adapter;

import creational.factory.Ticket;
import creational.factory.TicketFactory;
import creational.factory.TicketType;

/**
 * Adapter that converts an incompatible EmailMessage into a Ticket.
 *
 * PATTERN: Adapter (Object Adapter variant)
 * EmailMessage is the Adaptee (external format we cannot change).
 * TicketSource is the Target interface the system expects.
 * This class wraps EmailMessage and implements TicketSource so the system
 * can treat an email exactly like any other ticket source.
 *
 * Keyword detection logic lives here — centralised and isolated from
 * both EmailMessage and TicketFactory, following the Single Responsibility Principle.
 */
public class EmailTicketAdapter implements TicketSource {

    /** The external email being adapted. */
    private final EmailMessage email;

    /**
     * @param email The inbound email to convert. Must not be null.
     */
    public EmailTicketAdapter(EmailMessage email) {
        this.email = email;
    }

    /**
     * Convert the email into a typed Ticket by scanning the subject line for keywords.
     *
     * Detection rules (case-insensitive, first match wins):
     *   • "bug", "error", "crash"    → BugTicket
     *   • "complaint", "unhappy"     → ComplaintTicket
     *   • "feature", "request"       → FeatureRequestTicket
     *   • (anything else)            → InquiryTicket (safe default)
     *
     * Title  = email subject line
     * Description = "From: <sender>\n\n<body>"
     *
     * @return A Ticket whose type is inferred from the email subject.
     */
    @Override
    public Ticket toTicket() {
        // Normalise the subject to lower-case once for all comparisons
        String subjectLower = email.getSubject().toLowerCase();

        TicketType type;

        if (subjectLower.contains("bug") || subjectLower.contains("error")
                || subjectLower.contains("crash")) {
            type = TicketType.BUG;

        } else if (subjectLower.contains("complaint") || subjectLower.contains("unhappy")) {
            type = TicketType.COMPLAINT;

        } else if (subjectLower.contains("feature") || subjectLower.contains("request")) {
            type = TicketType.FEATURE_REQUEST;

        } else {
            // Default: treat ambiguous emails as general inquiries
            type = TicketType.INQUIRY;
        }

        // Combine sender info + body into the description for full context
        String description = "From: " + email.getSender() + "\n\n" + email.getBody();

        // Delegate ticket creation to the Factory (it handles ID assignment)
        return TicketFactory.createTicket(type, email.getSubject(), description);
    }
}
