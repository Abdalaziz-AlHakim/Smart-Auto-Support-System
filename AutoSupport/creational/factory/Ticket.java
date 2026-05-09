package creational.factory;

/**
 * Abstract base class for all ticket types in the AutoSupport system.
 *
 * PATTERN: Factory Method
 * This is the "Product" in the Factory Method pattern. TicketFactory creates
 * the correct subclass; callers always work through this abstract type.
 *
 * PATTERN: Decorator
 * TicketDecorator also extends this class so it can wrap any Ticket
 * transparently — the rest of the system cannot tell if a ticket has been
 * decorated or not.
 *
 * Fields are protected so subclasses and decorators can read them directly.
 */
public abstract class Ticket {

    protected final int id;           // Unique identifier assigned by TicketSystem
    protected final String title;     // Short summary entered by the agent
    protected final String description; // Full problem description
    protected Priority priority;      // NORMAL by default; set to URGENT by Decorator
    protected TicketStatus status;    // Lifecycle status; managed by State pattern

    /**
     * Base constructor called by every concrete ticket subclass and TicketFactory.
     *
     * @param id          Unique ID from TicketSystem.generateId()
     * @param title       Short summary of the issue
     * @param description Full details of the issue
     */
    public Ticket(int id, String title, String description) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.priority = Priority.NORMAL; // Default priority; Decorator overrides this
        this.status = TicketStatus.OPEN; // All tickets start in OPEN state
    }

    // ── Abstract methods ─────────────────────────────────────────────────────

    /**
     * @return The TicketType enum value (BUG, COMPLAINT, etc.)
     * Each subclass returns its own fixed type.
     */
    public abstract TicketType getType();

    /**
     * @return A human-readable label for display in the GUI table.
     * UrgentTicketDecorator prefixes this with "[URGENT] ".
     */
    public abstract String getTypeLabel();

    // ── Getters ──────────────────────────────────────────────────────────────

    public int getId()               { return id; }
    public String getTitle()         { return title; }
    public String getDescription()   { return description; }
    public Priority getPriority()    { return priority; }
    public TicketStatus getStatus()  { return status; }

    // ── Setters ──────────────────────────────────────────────────────────────

    /** Called by State classes (OpenState, InProgressState, etc.) during transitions. */
    public void setStatus(TicketStatus status) { this.status = status; }

    /** Called by UrgentTicketDecorator — elevates priority without subclassing. */
    public void setPriority(Priority priority) { this.priority = priority; }

    /**
     * Short display string used in the GUI ticket table.
     * UrgentTicketDecorator prepends "🔴 " to this.
     */
    @Override
    public String toString() {
        return String.format("[#%d] %s (%s) — %s", id, title, getTypeLabel(), status);
    }
}
