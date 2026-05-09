package creational.singleton;

import behavioral.observer.TicketEvent;
import behavioral.observer.TicketEventListener;
import behavioral.observer.TicketEventType;
import behavioral.state.TicketContext;
import creational.factory.Ticket;
import creational.factory.TicketStatus;
import structural.proxy.ITicketSystem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The single global registry for all tickets in the AutoSupport system.
 *
 * PATTERN: Singleton
 * Only one instance can ever exist. It is the authoritative source for:
 *   • The complete list of all submitted tickets
 *   • Auto-incrementing unique ticket IDs
 *   • The Observer event bus (subscriber list + notification dispatch)
 *
 * PATTERN: Observer (Subject role)
 * TicketSystem doubles as the event bus. Listeners register via subscribe()
 * and are notified whenever addTicket() or resolveTicket() is called.
 *
 * Thread safety note: for a demo application, lazy initialisation without
 * synchronisation is acceptable. In production, use an enum Singleton or
 * double-checked locking.
 */
public class TicketSystem implements ITicketSystem {

    // ── Singleton ────────────────────────────────────────────────────────────

    /** The one and only instance; created on first access. */
    private static TicketSystem instance;

    /** Private constructor prevents direct instantiation from outside. */
    private TicketSystem() {}

    /**
     * Returns the single TicketSystem instance, creating it on first call.
     *
     * @return The global TicketSystem instance.
     */
    public static TicketSystem getInstance() {
        if (instance == null) {
            instance = new TicketSystem();
        }
        return instance;
    }

    // ── State ────────────────────────────────────────────────────────────────

    /** All tickets ever submitted; order preserved by insertion. */
    private final List<Ticket> tickets = new ArrayList<>();

    /** Auto-incrementing counter; starts at 1 so ticket IDs are human-friendly. */
    private int idCounter = 1;

    /** Registered Observer listeners — notified on every lifecycle event. */
    private final List<TicketEventListener> listeners = new ArrayList<>();

    // ── Observer subscription ─────────────────────────────────────────────────

    /**
     * Register a new listener to receive ticket lifecycle events.
     * Call this at startup in Main.java for each listener (LogPanel, Console, Stats).
     *
     * @param listener The observer to add; must not be null.
     */
    public void subscribe(TicketEventListener listener) {
        listeners.add(listener);
    }

    /**
     * Broadcast a lifecycle event to every registered listener.
     * Called internally after addTicket() and resolveTicket().
     *
     * @param event The event to broadcast.
     */
    public void notifyListeners(TicketEvent event) {
        // Iterate a copy to avoid ConcurrentModificationException if a listener
        // unsubscribes itself during notification (defensive copy).
        for (TicketEventListener listener : new ArrayList<>(listeners)) {
            listener.onTicketEvent(event);
        }
    }

    // ── ITicketSystem implementation ──────────────────────────────────────────

    /**
     * Add a ticket to the registry and fire a CREATED event to all listeners.
     * Called by SupportFacade after the full construction pipeline completes.
     *
     * @param t The fully constructed (and optionally decorated) ticket.
     */
    @Override
    public void addTicket(Ticket t) {
        tickets.add(t);
        // Notify all observers that a new ticket has entered the system
        notifyListeners(new TicketEvent(t, TicketEventType.CREATED));
    }

    /**
     * Mark a ticket as RESOLVED and fire a RESOLVED event.
     *
     * PATTERN: State
     * Delegates to TicketContext.fromExisting().resolve() so illegal transitions
     * (resolving an OPEN or already-RESOLVED ticket) are rejected by the State
     * machine before any status change occurs.
     *
     * PATTERN: Proxy
     * Access to this method is guarded by TicketSystemProxy based on user role;
     * the Proxy throws SecurityException before this line is ever reached.
     *
     * @param t The ticket to resolve.
     * @throws IllegalStateException if the ticket is OPEN or already RESOLVED.
     */
    @Override
    public void resolveTicket(Ticket t) {
        // Delegate to the State machine — enforces legal transitions only
        TicketContext.fromExisting(t).resolve();
        // Notify observers only after a successful state transition
        notifyListeners(new TicketEvent(t, TicketEventType.RESOLVED));
    }

    /**
     * Return an unmodifiable view of all tickets so callers cannot mutate the list.
     *
     * @return Read-only list of all tickets in submission order.
     */
    @Override
    public List<Ticket> getAllTickets() {
        return Collections.unmodifiableList(tickets);
    }

    // ── ID generation ─────────────────────────────────────────────────────────

    /**
     * Generate the next unique ticket ID.
     * Called exclusively by TicketFactory so IDs are always assigned centrally.
     *
     * @return The next available integer ID (starts at 1).
     */
    public int generateId() {
        return idCounter++;
    }
}
