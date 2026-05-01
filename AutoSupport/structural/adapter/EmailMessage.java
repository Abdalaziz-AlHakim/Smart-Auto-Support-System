package structural.adapter;

/**
 * Represents an external email message with sender, subject, and body fields.
 * <p>
 * This class acts as the <b>Adaptee</b> in the Adapter pattern — it has an
 * incompatible interface that cannot be used directly as a {@code Ticket}.
 * </p>
 *
 * <b>Design Pattern:</b> Adapter (Adaptee)
 */
public class EmailMessage {

    /** The email sender address or name. */
    private String sender;

    /** The email subject line. */
    private String subject;

    /** The email body content. */
    private String body;

    /**
     * Constructs a new EmailMessage.
     *
     * @param sender  the sender of the email
     * @param subject the subject line
     * @param body    the body content
     */
    public EmailMessage(String sender, String subject, String body) {
        this.sender = sender;
        this.subject = subject;
        this.body = body;
    }

    /**
     * Returns the email sender.
     *
     * @return sender string
     */
    public String getSender() { return sender; }

    /**
     * Returns the email subject.
     *
     * @return subject string
     */
    public String getSubject() { return subject; }

    /**
     * Returns the email body.
     *
     * @return body string
     */
    public String getBody() { return body; }
}
