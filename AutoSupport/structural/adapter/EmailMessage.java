package structural.adapter;

/**
 * External email data object representing an inbound support email.
 *
 * PATTERN: Adapter (the "Adaptee")
 * This class simulates an external format that we cannot modify (e.g., from a
 * third-party mail library). EmailTicketAdapter bridges it to the Ticket format
 * the system expects — without touching this class at all.
 */
public class EmailMessage {

    private final String sender;   // Sender's email address
    private final String subject;  // Email subject line (used for type detection)
    private final String body;     // Full email body (used as ticket description)

    /**
     * @param sender  The email address of the person who sent the message
     * @param subject Subject line — EmailTicketAdapter scans this for keywords
     * @param body    Message body — used as the ticket description
     */
    public EmailMessage(String sender, String subject, String body) {
        this.sender  = sender;
        this.subject = subject;
        this.body    = body;
    }

    public String getSender()  { return sender; }
    public String getSubject() { return subject; }
    public String getBody()    { return body; }

    @Override
    public String toString() {
        return String.format("Email[from=%s, subject=%s]", sender, subject);
    }
}
