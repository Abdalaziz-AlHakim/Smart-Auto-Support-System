package creational.factory;

// TicketType, TicketStatus, TicketPriority are in the same package

/**
 * Abstract base class for all support tickets.
 * <p>
 * Every ticket has an ID, title, description, type, status, and priority.
 * Concrete subclasses define specific ticket types (Bug, Complaint, etc.).
 * </p>
 *
 * <b>Design Pattern:</b> Factory Method (Product)
 */
public abstract class Ticket {

    /** Unique identifier for this ticket. */
    protected int id;

    /** Short summary of the ticket issue. */
    protected String title;

    /** Detailed description of the ticket issue. */
    protected String description;

    /** The category/type of this ticket. */
    protected TicketType type;

    /** The current lifecycle status of this ticket. */
    protected TicketStatus status;

    /** The priority level of this ticket. */
    protected TicketPriority priority;

    /**
     * Constructs a new Ticket with the given ID, title, and description.
     * Status defaults to OPEN; priority defaults to NORMAL.
     *
     * @param id          unique ticket ID
     * @param title       short summary
     * @param description detailed description
     */
    public Ticket(int id, String title, String description) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = TicketStatus.OPEN;
        this.priority = TicketPriority.NORMAL;
    }

    /**
     * Returns a human-readable label for this ticket's type.
     *
     * @return the type label string
     */
    public abstract String getTypeLabel();

    /**
     * Returns the unique ticket ID.
     *
     * @return ticket ID
     */
    public int getId() { return id; }

    /**
     * Returns the ticket title.
     *
     * @return ticket title
     */
    public String getTitle() { return title; }

    /**
     * Returns the ticket description.
     *
     * @return ticket description
     */
    public String getDescription() { return description; }

    /**
     * Returns the ticket type enum value.
     *
     * @return ticket type
     */
    public TicketType getType() { return type; }

    /**
     * Returns the current ticket status.
     *
     * @return ticket status
     */
    public TicketStatus getStatus() { return status; }

    /**
     * Sets the ticket status.
     *
     * @param status the new status
     */
    public void setStatus(TicketStatus status) { this.status = status; }

    /**
     * Returns the ticket priority.
     *
     * @return ticket priority
     */
    public TicketPriority getPriority() { return priority; }

    /**
     * Sets the ticket priority.
     *
     * @param priority the new priority
     */
    public void setPriority(TicketPriority priority) { this.priority = priority; }

    /**
     * Returns a formatted string representation of this ticket.
     *
     * @return string in the format: [#id] TypeLabel - title | status | priority
     */
    @Override
    public String toString() {
        return "[#" + id + "] " + getTypeLabel() + " - " + title + " | " + status + " | " + priority;
    }
}
